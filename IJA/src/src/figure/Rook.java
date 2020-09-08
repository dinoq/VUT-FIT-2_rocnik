package figure;

import game.Field;

/**
 *
 * @author xfridr07 a xmarek69
 */
public class Rook extends Figure{
	
	/**
     * musí byt public kvůli vráceni tahům 
     */
    public boolean moved;
    
    /**
     * funkce na nastavení barvy figurky, její zobrazení a vybraní správného označení z Figures
     * @param c string pro natřídu
     * @see Figures
     */
	public Rook(String c) {
		super(c, Figures.ROOK);
		if(this.color.equals("b")) {
			url = "file:lib/figures/rook_b.png";
		}else {
			url = "file:lib/figures/rook_w.png";
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
            this.moved=true;
            this.field.removeFigure();
            field.setFigure(this);
            this.setField(field);
        }
        public boolean wasMoved(){
            return this.moved;
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
                        }
                    }else if (this.field.getRow()>field.getRow() && this.field.getCol()==field.getCol()){//D
                        temp=this.field.nextField(Field.Direction.D);
                        if (!(temp.getFigure()!=null && !temp.getFigure().getColor().equals(this.getColor()))){
                            while (temp!=field){
                                if (temp!=null && temp.getFigure()!=null && temp.getFigure().getColor().equals(this.getColor())){
                                    break;
                                }
                                temp=temp.nextField(Field.Direction.D);
                                if (temp==null){
                                    break;
                                }
                                if (temp.getFigure()!=null && !temp.getFigure().getColor().equals(this.getColor())){
                                    break;
                                }
                            }
                        }
                    }else if (this.field.getRow()<field.getRow() && this.field.getCol()==field.getCol()){//U
                        temp=this.field.nextField(Field.Direction.U);
                        if (!(temp.getFigure()!=null && !temp.getFigure().getColor().equals(this.getColor()))){
                            while (temp!=field){
                                if (temp!=null && temp.getFigure()!=null && temp.getFigure().getColor().equals(this.getColor())){
                                    break;
                                }
                                temp=temp.nextField(Field.Direction.U);
                                if (temp==null){
                                    break;
                                }
                                if (temp.getFigure()!=null && !temp.getFigure().getColor().equals(this.getColor())){
                                    break;
                                } 
                            }
                        }
                    }
                    if(temp==field)
                        success = true;
                    else 
                        success=false;              
               
            }return success;       
        
    }
}
      
