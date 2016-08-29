package breakthroughPP.players;

import breakthroughPP.preset.*;

/**
 * Class storing the probabilities of having certain neighbours
 * <p>
 * Georg-August University Goettingen
 * APP breakthroughPP
 * SoSe 2016
 * @author J.B.
 */
public class SectionProbabilities implements Setting {

	// positions
	private double tc;
	private double tl;
	private double tr;
	private double sl;
	private double sr;
	private double bc;
	private double bl;
	private double br;
	
	private int counter;

	private boolean editable;

// ==== Constructors ==============================================================================

	/**
	 * Default Constructor
	 */
	public SectionProbabilities(){

		tc = 0.0;
		tl = 0.0;
		tr = 0.0;
		sl = 0.0;
		sr = 0.0;
		bc = 0.0;
		bl = 0.0;
		br = 0.0;

		counter = 0;
		editable = true;
	}

// ==== Getter ====================================================================================
	public double getTopCenter() throws IllegalStateException{
		if(editable){
			throw new IllegalStateException("Analysis not completed. Call normalise() to"
				+ " finish.");
		}
		return tc;
	}

	public double getTopLeft() throws IllegalStateException{
		if(editable){
			throw new IllegalStateException("Analysis not completed. Call normalise() to"
				+ " finish.");
		}
		return tl;
	}

	public double getTopRight() throws IllegalStateException{
		if(editable){
			throw new IllegalStateException("Analysis not completed. Call normalise() to"
				+ " finish.");
		}
		return tr;
	}

	public double getSideLeft() throws IllegalStateException{
		if(editable){
			throw new IllegalStateException("Analysis not completed. Call normalise() to"
				+ " finish.");
		}

		return sl;
	}

	public double getSideRight() throws IllegalStateException{
		if(editable){
			throw new IllegalStateException("Analysis not completed. Call normalise() to"
				+ " finish.");
		}
		return sr;
	}

	public double getButtomLeft() throws IllegalStateException{
		if(editable){
			throw new IllegalStateException("Analysis not completed. Call normalise() to"
				+ " finish.");
		}
		return bl;
	}

	public double getButtomCenter() throws IllegalStateException{
		if(editable){
			throw new IllegalStateException("Analysis not completed. Call normalise() to"
				+ " finish.");
		}
		return bc;
	}

	public double getButtomRight() throws IllegalStateException{
		if(editable){
			throw new IllegalStateException("Analysis not completed. Call normalise() to"
				+ " finish.");
		}
		return br;
	}

// ==== Instance methods ==========================================================================
	/**
	 * normalise the frequencies with the number of inkerements and shut the inkrement funktions
	 */
	public void normalise(){

		editable = false;

		// Normalise
		tc = tc/counter;
		tl = tl/counter;
		tr = tr/counter;
		sl = sl/counter;
		sr = sr/counter;
		bc = bc/counter;
		bl = bl/counter;
		br = br/counter;
	}

	/**
	 * Incrementation of the field top center
	 */
	public void inkTopCenter() throws IllegalStateException{
		if(!editable){
			throw new IllegalStateException("Analysis already completed!");
		}
		tc = tc + 1.0;
		counter++;	
	}
	/**
	 * Incrementation of the field top left
	 */
	public void inkTopLeft() throws IllegalStateException{
		if(!editable){
			throw new IllegalStateException("Analysis already completed!");
		}
		tl = tl + 1.0;
		counter++;	
	}
	/** 
	 * Incrementation of the field top right
	 */
	public void inkTopRight() throws IllegalStateException{
		if(!editable){
			throw new IllegalStateException("Analysis already completed!");
		}
		tr = tr + 1.0;
		counter++;	
	}
	/**
	 * Incrementation of the field side left
	 */
	public void inkSideLeft() throws IllegalStateException{
		if(!editable){
			throw new IllegalStateException("Analysis already completed!");
		}
		sl = sl + 1.0;
		counter++;	
	}

	/**
	 * Incrementation of the field side right
	 */
	public void inkSideRight() throws IllegalStateException{
		if(!editable){
			throw new IllegalStateException("Analysis already completed!");
		}
		sr = sr + 1.0;
		counter++;	
	}
	/**
	 * Incrementation of the field buttom left
	 */
	public void inkButtomLeft() throws IllegalStateException{
		if(!editable){
			throw new IllegalStateException("Analysis already completed!");
		}
		bl = bl + 1.0;
		counter++;	
	}
	/**
	 * Incrementation of the field buttom center
	 */
	public void inkButtomCenter() throws IllegalStateException{
		if(!editable){
			throw new IllegalStateException("Analysis already completed!");
		}
		bc = bc + 1.0;
		counter++;	
	}
	/**
	 * Incrementation of the field buttom right
	 */
	public void inkButtomRight() throws IllegalStateException{
		if(!editable){
			throw new IllegalStateException("Analysis already completed!");
		}
		br = br + 1.0;
		counter++;	
	}
}

