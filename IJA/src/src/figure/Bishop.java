package figure;

import game.Field;

/**
 *
 * @author xfridr07 a xmarek69
 */
public class Bishop extends Figure{

    /**
     * funkce na nastavení barvy figurky, její zobrazení a vybraní správného označení z Figures
     * @param c string pro natřídu
     * @see Figures
     */
    public Bishop(String c) {
		super(c, Figures.BISHOP);
		if(this.color.equals("b")) {
			url = "file:lib/figures/bishop_b.png";
		}else {
			url = "file:lib/figures/bishop_w.png";
		}
		this.setImage();
	}

    /**
     * Přepisuje funkci třídy Figure pro zjišťovaní dostupných pohybů
     * @param field pole o kterém se rozhodne, jestli se tam může figurka pohybovat
     * @return jestli se na dané pole může figurka přemístit
     * @see Figure
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
                    }
                    if(temp==field)
                        success = true;
                    else 
                        success=false;              
              
                
            }
            return success;       
        }

}
