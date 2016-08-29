package breakthroughPP.helper;

/**
 * Class for recognizing operating systems
 * <p>
 * Georg-August University Goettingen
 * APP breakthroughPP
 * SoSe 2016
 * @author H.A.
 */
public class OSValidator {

	/** Contains a descriptive name of the operating system the JVM is running on */
    private static String OS = System.getProperty("os.name").toLowerCase();

    public static boolean isWindows(){
		return (OS.contains("win"));
	}

    public static boolean isMac() {
		return (OS.contains("mac"));
	}

    public static boolean isUnix(){
		return (OS.contains("nix") || OS.contains("nux") || OS.contains("aix"));
	}

    public static boolean isSolaris(){
		return OS.contains("sunos");
	}

}
