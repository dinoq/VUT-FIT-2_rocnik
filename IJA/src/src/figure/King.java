package figure;

import game.Board;
import static game.Board.BOARD_SIZE;
import game.Field;
import game.Field.Direction;

/**
 *
 * @author xfridr07 a xmarek69
 */
public class King extends Figure{

	/**
     * musí byt public kvůli vráceni tahům 
     */
    public boolean moved;
    
    boolean vyhod=true;
    /**
     * funkce na nastavení barvy figurky, její zobrazení a vybraní správného označení z Figures
     * @param c string pro natřídu
     * @see Figures
     */
	public King(String c) {
                super(c, Figures.KING);
		if(this.color.equals("b")) {
			url = "file:lib/figures/king_b.png";
		}else {
			url = "file:lib/figures/king_w.png";
		}
                this.moved=false;
		this.setImage();
	}
	
    /**
     * Přepisuje funkci třídy Figure pro pohyb figurky, bylo nutné kvůli rošádě
     * @param field pole kam se figurka má pohnout
     * @see Figure
     */
    @Override 
    public void moveTo(Field field){
        if (!moved && field.getCol()-2==this.field.getCol()){
            Figure vez=field.nextField(Direction.R).getFigure();
            field.nextField(Direction.R).removeFigure();
            field.nextField(Direction.L).setFigure(vez);
                
        }else if (!moved && field.getCol()+2==this.field.getCol()){
            Figure vez=field.nextField(Direction.L).nextField(Direction.L).getFigure();
            field.nextField(Direction.L).nextField(Direction.L).removeFigure();
            field.nextField(Direction.R).setFigure(vez);
        } 
        this.moved=true;
        this.field.removeFigure();
        field.setFigure(this);
        this.setField(field);
    }

    /**
     * Přepisuje funkci třídy Figure pro zjišťovaní dostupných pohybů
     * @param field pole o kterém se rozhodne, jestli se tam může figurka pohybovat
     * @return jestli se na dane pole může figurka přemístit
     * @see Figure
     */
	@Override
	public boolean canMoveTo(Field field) {
		boolean success = false;

                if(field.getFigure()!=null && field.getFigure().getColor().equals(this.getColor())) {
                            success = false;
                }else{
                    
                    if (this.field.getRow() == (field.getRow()-1) && (field.getCol()+1) == this.field.getCol()) {
                            success = isThreatened(field);
                    }else if(this.field.getRow() == (field.getRow()-1) && (field.getCol()-1) == this.field.getCol()) {
                            success = isThreatened(field);
                    }else if(this.field.getRow() == (field.getRow()+1) && (field.getCol()-1) == this.field.getCol()) {
                            success = isThreatened(field);
                    }else if(this.field.getRow() == (field.getRow()+1) && (field.getCol()+1) == this.field.getCol()) {
                            success = isThreatened(field);
                    }else if(this.field.getRow() == (field.getRow()-1) && (field.getCol()) == this.field.getCol()) {
                            success = isThreatened(field);
                    }else if(this.field.getRow() == (field.getRow()+1) && (field.getCol()) == this.field.getCol()) {
                            success = isThreatened(field);
                    }else if(this.field.getRow() == (field.getRow()) && (field.getCol()-1) == this.field.getCol()) {
                            success = isThreatened(field);
                    }else if(this.field.getRow() == (field.getRow()) && (field.getCol()+1) == this.field.getCol()) {
                            success = isThreatened(field);
                    }else if (!moved){
                        Field temp=this.field;
                        if (this.field.getRow()==field.getRow() && this.field.getCol()>field.getCol()){//L
                            temp=this.field.nextField(Field.Direction.L);
                            if (!(temp.getFigure()!=null && !temp.getFigure().getColor().equals(this.getColor()))){
                                while (temp!=field){
                                    if (temp!=null && temp.getFigure()!=null && temp.getFigure().getColor().equals(this.getColor())){
                                        break;
                                    }
                                    temp=temp.nextField(Field.Direction.L);
                                    if (temp==null){
                                        break;
                                    }
                                    if (temp.getFigure()!=null && !temp.getFigure().getColor().equals(this.getColor())){
                                        break;
                                    }
                                }
                            }
                            //velka rosada,
                            if (temp!=null && temp.nextField(Direction.L)!=null&& temp.nextField(Direction.L).nextField(Direction.L)!=null &&temp.nextField(Direction.L).isEmpty() && temp.nextField(Direction.L).nextField(Direction.L).getFigure()!=null&& temp.nextField(Direction.L).nextField(Direction.L).getFigure().getType()==Figures.ROOK){
                                if (!((Rook)temp.nextField(Direction.L).nextField(Direction.L).getFigure()).wasMoved()){                                                            
                                    if (this.field.getRow()==field.getRow() && this.field.getCol()==field.getCol()+2){
                                        success = isThreatened(field);
                                    }
                                }
                            }
                        }else if (this.field.getRow()==field.getRow() && this.field.getCol()<field.getCol()){//R

                            temp=this.field.nextField(Field.Direction.R);
                            if (!(temp.getFigure()!=null && !temp.getFigure().getColor().equals(this.getColor()))){
                                while (temp!=field){
                                    if (temp!=null && temp.getFigure()!=null && temp.getFigure().getColor().equals(this.getColor())){
                                        break;
                                    }
                                    temp=temp.nextField(Field.Direction.R);
                                    if (temp==null){
                                        break;
                                    }
                                    if (temp.getFigure()!=null && !temp.getFigure().getColor().equals(this.getColor())){
                                        break;
                                    }
                                }
                            }//mala rosada
                            if (temp!=null && temp.nextField(Direction.R)!=null&& temp.nextField(Direction.R).getFigure()!=null&& temp.nextField(Direction.R).getFigure().getType()==Figures.ROOK){
                                if (!((Rook)temp.nextField(Direction.R).getFigure()).wasMoved()){
                                    if (this.field.getRow()==field.getRow() && this.field.getCol()==field.getCol()-2){
                                        success = isThreatened(field);
                                    }
                                }
                            }
                        }                   
                    }
                    else{
                        success = false;
                    }

		}
		return success;
	}
	
	
	private Field getFirstField(Field f) {		
		if(f.nextField(Direction.L) == null) {
			if(f.nextField(Direction.D) == null) {
				return f;
			}else {
				return getFirstField(f.nextField(Direction.D));
			}
		}else {
			return getFirstField(f.nextField(Direction.L));
		}
	}

    /**
     * Funkce na zjištění jestli je král v šachu.
     * @param board board na kterem se hraje
     * @return Pokud je král v šachu, vrátí true jinak false.
     */
    public boolean inCheck(Board board){
        boolean success=false;
        Field f;
    	Figure opponentsFigure;
        String barve=(this.field.getFigure().getColor());
        
        /*this.field.removeFigure();
        this.field.setFigure(new Pawn(barve));*/
    	for(int i = 1; i <= Board.BOARD_SIZE; i++) {
            for(int j = 1; j <= BOARD_SIZE; j++){
                f = board.getField(i, j);
                opponentsFigure = f.getFigure();
                if(opponentsFigure != null && !opponentsFigure.getColor().equals(barve)) {
                    if(opponentsFigure.canMoveTo(this.field)) {
                            success = true;
                    }
                }
            }
    	}
       /* this.field.removeFigure();
        this.field.setFigure(this);*/
        return success;
    }

    /**
     * Funkce na zjistění, jestli je král všachu a pokud ano, tak jestli se z něho může dostat nebo jestli je to šach mat.
     * @return Jestli je šach mat vrátí false, jinak true.
     */
    public boolean canCancelCheck(){
            boolean success=false;
            Field tempy=getFirstField(this.field);
            Field tempx;
            Field tempa;
            Field tempb;
            Figure odloznaenemy=this.field.getFigure();
            boolean vratit=false;
            if (isThreatened(this.field)){
                success=true;
            }else if (isThreatened(this.field.nextField(Direction.D))||isThreatened(this.field.nextField(Direction.R))||isThreatened(this.field.nextField(Direction.L))||isThreatened(this.field.nextField(Direction.D))){
                success=true;
            }else if (isThreatened(this.field.nextField(Direction.RD))||isThreatened(this.field.nextField(Direction.LU))||isThreatened(this.field.nextField(Direction.LD))||isThreatened(this.field.nextField(Direction.RU))){
                success=true;
            }else{         
                while(tempy!=null && !success){
                    if (tempy.getFigure()!=null && tempy.getFigure().getColor().equals(this.getColor())&&tempy.getFigure().getType()!=Figures.KING){
                        tempx=tempy;
                        while(tempx!=null && !success){                           
                            if (tempx.getFigure()!=null && tempx.getFigure().getColor().equals(this.getColor())&&tempx.getFigure().getType()!=Figures.KING){
                                //System.out.println("FIGURKA"+tempx.getFigure().getType()+tempx.getCol()+tempx.getRow());        
                                tempa=getFirstField(this.field);     
                                while(tempa!=null && !success){                                          
                                               tempb=tempa;
                                               while(tempb!=null && !success){ 
                                                   if(tempx.getFigure().canMoveTo(tempb)){
                                                       if(tempb.getFigure()!=null){
                                                           odloznaenemy=tempb.getFigure();
                                                           vratit=true;
                                                           tempb.removeFigure();
                                                       }
                                                       tempx.getFigure().moveTo(tempb);
                                                            Field firstField=getFirstField(this.field);
                                                            boolean succcess=SearchThreatenedInBoard(firstField,this.field);
                                                        if (succcess){
                                                        }
                                                        tempb.getFigure().moveTo(tempx);                                                                
                                                        if (vratit){
                                                            tempb.setFigure(odloznaenemy);
                                                            vratit=false;
                                                        }
                                                   }
                                               tempb=tempb.nextField(Direction.R);
                                               }  
                                           
                                           tempa=tempa.nextField(Direction.U);
                                        }
                            }
                        tempx=tempx.nextField(Direction.R);
                        }  
                    }
                    tempy=tempy.nextField(Direction.U);
                }
            }
            return success;
        }
    
	private boolean SearchThreatenedInBoard(Field firstField,Field f) {
		boolean success = true;
                Field temp=firstField;
                if (f.isEmpty()){
                    while (firstField!=null && success){  
                        if (firstField.getFigure()!=null && !firstField.getFigure().getColor().equals(this.getColor())){

                            //f.setFigure(new Pawn(this.getColor()));
                            if(firstField.getFigure().canKickOut(f)){
                                success=false; 
                            }
                           // f.removeFigure();
                        }
                        temp=firstField;
                        while(temp!=null && success){
                            temp=temp.nextField(Direction.U);
                            if (temp!=null && temp.getFigure()!=null && !temp.getFigure().getColor().equals(this.getColor())){
                               // f.setFigure(new Pawn(this.getColor()));
                                if(temp.getFigure().canKickOut(f)){
                                    success=false;
                                }
                               // f.removeFigure();
                            }
                        }
                        firstField=firstField.nextField(Direction.R);
                    }
                    if (temp!=null && temp.getFigure()!=null && !temp.getFigure().getColor().equals(this.getColor())){
                       // f.setFigure(new Pawn(this.getColor()));
                        if(temp.getFigure().canKickOut(f)){
                            success=false;
                        }
                        //f.removeFigure();
                    }
                }else{ 
                    Figure figurka= f.getFigure();
                   f.removeFigure();                  
                    while (firstField!=null && success){  
                        if (firstField.getFigure()!=null && !firstField.getFigure().getColor().equals(this.getColor())){

                           f.setFigure(new Pawn(this.getColor()));
                            if(firstField.getFigure().canKickOut(f)){
                                success=false; 
                            }
                           f.removeFigure();
                        }
                        temp=firstField;
                        while(temp!=null && success){
                            temp=temp.nextField(Direction.U);
                            if (temp!=null && temp.getFigure()!=null && !temp.getFigure().getColor().equals(this.getColor())){
                               f.setFigure(new Pawn(this.getColor()));
                                if(temp.getFigure().canKickOut(f)){
                                    success=false;
                                }
                               f.removeFigure();
                            }
                        }
                        firstField=firstField.nextField(Direction.R);
                    }
                    if (temp!=null && temp.getFigure()!=null && !temp.getFigure().getColor().equals(this.getColor())){
                        //f.setFigure(new Pawn(this.getColor()));
                        if(temp.getFigure().canKickOut(f)){
                            success=false;
                        }
                        f.removeFigure();
                    }
                f.setFigure(figurka);
                }
                
		return success;
	}
	
    private boolean isThreatened(Field f){
        if (f==null){
            return false;
        }
        //this.field.removeFigure();
        Field firstField=getFirstField(f);
        boolean success=SearchThreatenedInBoard(firstField,f);
        //this.field.setFigure(this);
        return success;
    }

    /**
     * Přepisuje funkci třídy Figure pro zjišťovaní, jestli může daná figurka vyhodit někoho na daném políčku
     * @param field pole, na kterém může vyhazovat
     * @return jestli může vyhodit figurku na poli zadané parametrem
     * @see Figure
     */
    @Override
    public boolean canKickOut(Field field){

            
        boolean success = false;

         if(field.getFigure()!=null && field.getFigure().getColor().equals(this.getColor())) {
                    success = false;
        }else{
                if (this.field.getRow() == (field.getRow()-1) && (field.getCol()+1) == this.field.getCol()) {
                        success = true;
                }else if(this.field.getRow() == (field.getRow()-1) && (field.getCol()-1) == this.field.getCol()) {
                        success = true;
                }else if(this.field.getRow() == (field.getRow()+1) && (field.getCol()-1) == this.field.getCol()) {
                        success = true;
                }else if(this.field.getRow() == (field.getRow()+1) && (field.getCol()+1) == this.field.getCol()) {
                        success = true;
                }else if(this.field.getRow() == (field.getRow()-1) && (field.getCol()) == this.field.getCol()) {
                        success = true;
                }else if(this.field.getRow() == (field.getRow()+1) && (field.getCol()) == this.field.getCol()) {
                        success = true;
                }else if(this.field.getRow() == (field.getRow()) && (field.getCol()-1) == this.field.getCol()) {
                        success = true;
                }else if(this.field.getRow() == (field.getRow()) && (field.getCol()+1) == this.field.getCol()) {
                        success = true;
                }

        }
        return success;
    }

}
