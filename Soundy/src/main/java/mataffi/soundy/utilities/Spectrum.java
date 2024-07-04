package mataffi.soundy.utilities;


import mataffi.soundy.config.AudioParams;

public class Spectrum {

	public static double[][] compute(byte[] audioTimeDomain) {

		final int totalSize = audioTimeDomain.length;

		int chunks = totalSize / AudioParams.chunkSize;

		double[][] resultsMag = new double[chunks][];

		Complex[][] resultsComplex = new Complex[chunks][];

		for (int i = 0; i < chunks; i++) {
			Complex[] complex = new Complex[AudioParams.chunkSize];
			for(int j = 0; j < AudioParams.chunkSize; j++) {
				complex[j] = new Complex(audioTimeDomain[(i*AudioParams.chunkSize)+j], 0);
			}
			resultsComplex[i] = FFT.fft(complex);
			resultsMag[i]= new double[AudioParams.chunkSize];
			for (int j = 0; j < AudioParams.chunkSize; j++) {
				resultsMag[i][j] = Math.log(resultsComplex[i][j].abs() + 1);
			}
		}                       
		return resultsMag;
	}
}