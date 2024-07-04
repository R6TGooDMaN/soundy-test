package mataffi.soundy.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mataffi.soundy.config.AudioParams;
import mataffi.soundy.config.KeyPoint;
import mataffi.soundy.utilities.Serialization;
import mataffi.soundy.utilities.Spectrum;
import mataffi.soundy.utilities.HashingFunctions;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
@Component
@AllArgsConstructor
public class AudioRecognizer {
	AudioFormat audioFormat = new AudioFormat(AudioParams.sampleRate,
			AudioParams.sampleSizeInBits, AudioParams.channels,
			AudioParams.signed, AudioParams.bigEndian);

	private Map<Long, List<KeyPoint>> hashMapSongRepository;

	@Getter
	private boolean running;

	@Getter
	private String bestSong;

	private TargetDataLine line;

	public AudioRecognizer() {
		this.hashMapSongRepository = Serialization.deserializeHashMap();
		this.running = true;
	}

	public void stopListening(){
		if(line==null || !line.isRunning()) return;
		this.running = false;
		line.close();
	}

	protected TargetDataLine objFactory() throws LineUnavailableException {
		DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);
		return (TargetDataLine)AudioSystem.getLine(info);

	}

	public void listening(String songId, boolean isMatching) throws LineUnavailableException {
			this.running = true;
			line = objFactory();
			line.open(audioFormat);
			line.start();

		Thread listeningThread = new Thread(new Runnable() {

			@Override
			public void run() {
				ByteArrayOutputStream outStream = new ByteArrayOutputStream();

				byte[] buffer = new byte[AudioParams.bufferSize];               
				int n = 0;
				try {
					while (running) {
						int count = line.read(buffer, 0, buffer.length);

						if (count > 0) {
							outStream.write(buffer, 0, count);
						}
					}

					byte[] audioTimeDomain = outStream.toByteArray();

					double [][] magnitudeSpectrum = Spectrum.compute(audioTimeDomain);

					shazamAction(magnitudeSpectrum, songId, isMatching);

					outStream.close();                    

					Serialization.serializeHashMap(hashMapSongRepository);
				} catch (IOException e) {
					System.err.println("I/O exception " + e);
					System.exit(-1);
				}
			}
		});

		listeningThread.start();

		System.out.println("Press ENTER key to stop listening...");
		try {
			Thread.sleep(15000);
		} catch (InterruptedException ex) {
			Logger.getLogger(AudioRecognizer.class.getName()).log(Level.SEVERE, null, ex);
		}
		this.running = false;
		line.close();
	}   


	private void shazamAction(double[][] magnitudeSpectrum, String songId, boolean isMatching) {
		Map<String, Map<Integer,Integer>> matchMap = 
				new HashMap<String, Map<Integer,Integer>>(); 

		for (int c = 0; c < magnitudeSpectrum.length; c++) {
			long hash = computeHashEntry(magnitudeSpectrum[c]);

			if (!isMatching) {
				KeyPoint point = new KeyPoint(songId, c);
				List <KeyPoint> keyPointList;
				if((keyPointList = hashMapSongRepository.get(hash)) == null){
					keyPointList  = new ArrayList<KeyPoint>();
					hashMapSongRepository.put(hash, keyPointList);
				}
				keyPointList.add(point);
			}
			else {
				List <KeyPoint> keyPointList;
				if((keyPointList = hashMapSongRepository.get(hash)) != null) {
					for(int i = 0; i < keyPointList.size(); i++) {
						int offSet = Math.abs(keyPointList.get(i).getTimestamp() - c);
						Map<Integer, Integer> tmp;
						if((tmp = matchMap.get(keyPointList.get(i).getSongId())) == null) {
							tmp = new HashMap<Integer, Integer>();
							tmp.put(offSet, 1);
							matchMap.put(keyPointList.get(i).getSongId(), tmp);
						}else {
							if(tmp.get(offSet) == null){
								tmp.put(offSet, 1);
							}else {
								tmp.put(offSet, tmp.get(offSet)+1);
							}
						}
					}
				}
			}            
		}
		if (isMatching) {
			showBestMatching(matchMap);
		}
	}

	private int getIndex(int freq) {

		int i = 0;
		while (AudioParams.range[i] < freq) {
			i++;
		}
		return i;
	}  

	private long computeHashEntry(double[] chunk) {

		double highscores[] = new double[AudioParams.range.length];
		int frequencyPoints[] = new int[AudioParams.range.length];

		for (int freq = AudioParams.lowerLimit; freq < AudioParams.unpperLimit - 1; freq++) {
			double mag = chunk[freq];
			int index = getIndex(freq);
			if (mag > highscores[index]) {
				highscores[index] = mag;
				frequencyPoints[index] = freq;
			}
		}
		return HashingFunctions.hash1(frequencyPoints[0], frequencyPoints[1], 
				frequencyPoints[2],frequencyPoints[3],AudioParams.fuzzFactor);
	}

	private void showBestMatching(Map<String, Map<Integer, Integer>> matchMap) {
		String song;
		bestSong = "Not found";
		int offset, counter, bestCounter = 4;
		Iterator songIterator = matchMap.entrySet().iterator();
		while(songIterator.hasNext()) {
			Map.Entry e = (Map.Entry)songIterator.next();
			song = (String) e.getKey();
			Map<Integer,Integer> tmp = (Map<Integer, Integer>) e.getValue();
			Iterator offsetIterator = tmp.entrySet().iterator();
			while(offsetIterator.hasNext()) {
				Map.Entry d = (Map.Entry) offsetIterator.next();
				offset = (int) d.getKey();
				counter = tmp.get(offset);
				if(counter > bestCounter) {
					bestCounter = counter;
					this.bestSong = song;
				}
			}
		}

		System.out.println("Best song: " + bestSong);
	}
}