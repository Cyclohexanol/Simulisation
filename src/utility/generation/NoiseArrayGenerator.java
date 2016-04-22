package utility.generation;

public class NoiseArrayGenerator 
{
	public static float[][] generateNoise(int width, int height, int seed, float layerF, float weight, int loops) {
        new SimplexNoise(seed);
        float[][] noise = new float[width][height];
       
        for(int i = 0; i < loops; i++) {
                for(int x = 0; x < width; x++) {
                        for(int y = 0; y < height; y++) {
                                noise[x][y] += (float) SimplexNoise.noise(x * layerF, y * layerF) * weight;
                                noise[x][y] = clamp(noise[x][y], -1.0f, 1.0f);
                        }
                }
                layerF *= 3.5f;
                weight *= 0.5f;
        }
       
        return noise;
	}
	
	public static float[][] generateNoise(int width, int height, int seed) {
        return generateNoise(width, height, seed, 0.003f, 1f, 3);
	}
	
	static private float clamp (float value, float min, float max) {
		if (value < min) return min;
		if (value > max) return max;
		return value;
	}
	
}
