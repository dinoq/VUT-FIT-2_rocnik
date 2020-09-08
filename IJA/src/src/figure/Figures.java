package figure;

/**
 *
 * @author xfridr07 a xmarek69
 */
public enum Figures {
	KING("K"),
	QUEEN("D"),
	BISHOP("S"),
	KNIGHT("J"),
	ROOK("V"),
	PAWN("p");
	
	private final String name;       

    private Figures(String s) {
        name = s;
    }
    
    /**
     * Zjištění typu figurky podle jejího názvu - prvního písmenka
     * @param s první písmeno figurky
     * @return označení figurky
     */
    public static Figures getFigureTypeByName(String s) {
    	Figures f = null;
    	switch (s) {
		case "K":
			f = Figures.KING;
			break;
		case "D":
			f = Figures.QUEEN;
			break;
		case "S":
			f = Figures.BISHOP;
			break;
		case "J":
			f = Figures.KNIGHT;
			break;
		case "V":
			f = Figures.ROOK;
			break;
		case "p":
			f = Figures.PAWN;
			break;

		default:
			break;
		}
    	
    	
    	return f;
    }
    
    /**
     * Zjištění prvního písmenka figurky, která se používá jako její značka
     * @return Značku figurky
     */
    public String getFirstCharOfName() {
    	return this.name;
    }
}
