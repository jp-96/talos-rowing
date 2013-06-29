package org.nargila.robostroke.media.vlc;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.nargila.robostroke.common.ClockTime;

import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaPlayer;

public class VlcFrameSteppingPlayerPanel extends JPanel {
	
	public interface TimeChangeListener {
		public void onTimeChanged(long time);
	}
	
	private JButton btnPlay;
	private JButton btnNext;
	private TimeChangeListener timeListener;
	private JSlider slider;
	private EmbeddedMediaPlayerComponent vlc;
	private JButton btnSkipBack;
	private JButton btnSkipForeward;
	private JLabel lblTime;

	/**
	 * Create the panel.
	 */
	public VlcFrameSteppingPlayerPanel() {
		setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		add(panel, BorderLayout.SOUTH);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		slider = new JSlider();
		slider.setValue(0);
		slider.setMaximum(1000);
		slider.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				vlc.getMediaPlayer().setPosition((float)slider.getValue() / slider.getMaximum());
			}
		});
		
		panel.add(slider);
		
		JPanel panel_1 = new JPanel();
		panel.add(panel_1);
		panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.X_AXIS));
		
		btnPlay = new JButton(">|=");
		btnPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				vlc.getMediaPlayer().pause();
			}
		});
		panel_1.add(btnPlay);
		
		btnSkipBack = new JButton("-3");
		btnSkipBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				vlc.getMediaPlayer().skip(-3000);
				updateTime();
			}
		});
		panel_1.add(btnSkipBack);
		
		btnSkipForeward = new JButton("+3");
		btnSkipForeward.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				vlc.getMediaPlayer().skip(3000);
				updateTime();
			}
		});
		panel_1.add(btnSkipForeward);
		
		btnNext = new JButton(">>");
		btnNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				vlc.getMediaPlayer().nextFrame();
				updateTime();
			}
		});
		panel_1.add(btnNext);
		
		JPanel panel_3 = new JPanel();
		panel_1.add(panel_3);
		
		lblTime = new JLabel("00:00:00,000");
		panel_3.add(lblTime);
		
		vlc = new EmbeddedMediaPlayerComponent() {
		    @Override
		    public void timeChanged(MediaPlayer mediaPlayer, long newTime) {
		    	updateTime();
		    }

		    @Override
		    public void positionChanged(MediaPlayer mediaPlayer, float newPosition) {
		    	updateTime();
		    }
		};
		
		add(vlc, BorderLayout.CENTER);
		
	}

	public void setTimeListener(TimeChangeListener listener) {
		this.timeListener = listener;		
	}
	
	private void updateTime() {
		long time = vlc.getMediaPlayer().getTime();
		timeListener.onTimeChanged(time);
		lblTime.setText(ClockTime.fromMillis(time).toString());
		slider.setValue((int) (vlc.getMediaPlayer().getPosition() * slider.getMaximum()));
	}

	public static void main(String[] args) {
		
		final VlcFrameSteppingPlayerPanel player = new VlcFrameSteppingPlayerPanel();
		
	    JFrame f = new JFrame("Test Player");
//	    f.setIconImage(new ImageIcon(MinimalTestPlayer.class.getResource("/icons/vlcj-logo.png")).getImage());
	    f.setSize(800, 600);
	    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    f.addWindowListener(new WindowAdapter() {
	      @Override
	      public void windowClosing(WindowEvent e) {
	    	  player.vlc.release();
	      }
	    });
	    
	    JPanel p = new JPanel(new BorderLayout());
	    
	    final JLabel lblTime = new JLabel();
	    lblTime.setText("00:00:00,000");
	    lblTime.setFont(Font.getFont(Font.MONOSPACED));

	    p.add(lblTime, BorderLayout.SOUTH);
	    
	    p.add(player, BorderLayout.CENTER);
	    
	    f.setContentPane(p);
	    
	    f.setVisible(true);
	    
	    player.setTimeListener(new TimeChangeListener() {
			
			@Override
			public void onTimeChanged(long time) {
				updateTime(lblTime, time);
			}

			private void updateTime(final JLabel lblTime, long time) {
				ClockTime t = ClockTime.fromMillis(time);
				lblTime.setText(t.toString());
			}
		});
	    
	    player.play(args[0]);
	}
	
	public void play(String mrl) {
		vlc.getMediaPlayer().playMedia(mrl);		
	}

	public void stop() {
		vlc.getMediaPlayer().stop();
		vlc.release();
	}
}