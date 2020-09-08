package game_logic;

import java.util.Date;
import java.util.TimerTask;

import javafx.application.Platform;
import javafx.scene.control.RadioButton;

/**
 *
 * @author xfridr07 a xmarek69
 */
public class ChessTimer implements Runnable{

	public Thread t;
	private Long timeToSleep = 1000L;
	
	Controller controller;
	
	boolean success = true;

    /**
     *
     * @param c odkaz na controller
     */
    public ChessTimer(Controller c) {
		controller = c;
	}
    /**
     *
     * přepsani funkc na spouštění časovače
     */
	@Override
	public void run() {
		while(true) {
			if(!controller.getNextMoveButton().isDisabled()) {
				Platform.runLater(() -> 
					{
						if(((RadioButton) controller.getDirectionPlayToggleGroup().getSelectedToggle()).getText() == "dopředu") {
							controller.nextMove();
						}else {
							controller.prevMove();
						}
					});
			}else {
				this.interruptThread();
				Platform.runLater(() -> controller.getPlayButton().setText("play"));
			}
			
			try {
				Thread.sleep(timeToSleep);
			} catch (InterruptedException e) {
				t = null;
				//System.out.println("Končím play");
				break;
			}
		}
		//System.out.println("terminated");
	}
	
    /**
     *
     * @param time čas přehrávání
     */
    public void play(String time) {
		if(t == null) {
			this.timeToSleep = Math.round(Double.parseDouble(time) * 1000);
			t = new Thread (this, "vlakno");
			t.start ();
		}else {
			t.interrupt();
		}
	}
	
    /**
     *
     */
    public void interruptThread() {
		//System.out.println("inter");
		if(t != null) {
			t.interrupt();
		}
	}
}
