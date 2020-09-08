package figure;

import game.Field;

/**
 *
 * @author xfridr07 a xmarek69
 */
public class Queen extends Figure{
    /**
     * funkce na nastavení barvy figurky, její zobrazení a vybraní správného označení z Figures
     * @param c string pro natřídu
     * @see Figures
     */
	public Queen(String c) {
		super(c, Figures.QUEEN);
		if(this.color.equals("b")) {
			url = "file:lib/figures/queen_b.png";
		}else {
			url = "file:lib/figures/queen_w.png";
		}
		this.setImage();
	}
     /**
     * Prepisuje funkci natridy figure pro zjistovani dostupnych pohybu.
     * U královny jde o zkombinování pohybů věže a střelce
     * @param field policko o kterem se rozhodne jestli se tam muze figurka pohybovat
     * @return jestli se na dane policko muze figurka premistit
     */
	@Override
	public boolean canMoveTo(Field field) {
            boolean success = false;


            if(field.getFigure()!=null && field.getFigure().getColor().equals(this.getColor())) {
                success = false;
            }else{
                
                    Field temp=this.field;
                    if (this.field.getRow()>field.getRow() && this.field.getCol()>field.getCol()){//LD
                        temp=this.field.nextField(Field.Direction.LD);
                        if (!(temp.getFigure()!=null && !temp.getFigure().getColor().equals(this.getColor()))){
                            while (temp!=field){
                                if (temp!=null && temp.getFigure()!=null && temp.getFigure().getColor().equals(this.getColor())){
                                    break;
                                }
                                temp=temp.nextField(Field.Direction.LD);
                                if (temp==null){
                                    break;
                                }
                                if (temp.getFigure()!=null && !temp.getFigure().getColor().equals(this.getColor())){
                                    break;
                                }
                            }
                        }
                    }else if (this.field.getRow()<field.getRow() && this.field.getCol()<field.getCol()){//RU
                        
                        temp=this.field.nextField(Field.Direction.RU);
                        if (!(temp.getFigure()!=null && !temp.getFigure().getColor().equals(this.getColor()))){
                            while (temp!=field){
                                if (temp!=null && temp.getFigure()!=null && temp.getFigure().getColor().equals(this.getColor())){
                                    break;
                                }
                                temp=temp.nextField(Field.Direction.RU);
                                if (temp==null){
                                    break;
                                }
                                if (temp.getFigure()!=null && !temp.getFigure().getColor().equals(this.getColor())){
                                    break;
                                }
                            }
                        }
                    }else if (this.field.getRow()>field.getRow() && this.field.getCol()<field.getCol()){//RD
                        temp=this.field.nextField(Field.Direction.RD);
                        if (!(temp.getFigure()!=null && !temp.getFigure().getColor().equals(this.getColor()))){
                            while (temp!=field){
                                if (temp!=null && temp.getFigure()!=null && temp.getFigure().getColor().equals(this.getColor())){
                                    break;
                                }
                                temp=temp.nextField(Field.Direction.RD);
                                if (temp==null){
                                    break;
                                }
                                if (temp.getFigure()!=null && !temp.getFigure().getColor().equals(this.getColor())){
                                    break;
                                }
                            }
                        }
                    }else if (this.field.getRow()<field.getRow() && this.field.getCol()>field.getCol()){//LU
                        temp=this.field.nextField(Field.Direction.LU);
                        if (!(temp.getFigure()!=null && !temp.getFigure().getColor().equals(this.getColor()))){
                            while (temp!=field){
                                if (temp!=null && temp.getFigure()!=null && temp.getFigure().getColor().equals(this.getColor())){
                                    break;
                                }
                                temp=temp.nextField(Field.Direction.LU);
                                if (temp==null){
                                    break;
                                }
                                if (temp.getFigure()!=null && !temp.getFigure().getColor().equals(this.getColor())){
                                    break;
                                } 
                            }
                        }
                    }else if (this.field.getRow()==field.getRow() && this.field.getCol()>field.getCol()){//L
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
              
                
            }
            return success;       
        }

}
