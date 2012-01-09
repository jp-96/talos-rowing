package org.nargila.robostroke.ui;


import java.awt.Canvas;
import java.awt.Graphics;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JFrame;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SwingCanvasTest {

	private interface Painter {
		void paint(SwingCanvas canvas);
	}
	
	
	
	private UILiaison uiLiaison;
	private Canvas impl; 
	private SwingCanvas canvas;
	private JFrame frame;
	private final RSPaint paint = new SwingPaint() {
		{
			setStyle(PaintStyle.FILL);
		}
	};
	
	private Painter painter;
	
	@Before
	public void setUp() throws Exception {
		
		impl =  new Canvas() {
			@Override
			public void paint(Graphics g) {
				painter.paint(new SwingCanvas(this, g));
			}
		};
		
		uiLiaison = new SwingUILiaison(impl);
		canvas = new SwingCanvas(impl, null);
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		impl.setSize(400, 400);
		
		frame.getContentPane().add(impl);
		
		frame.pack();
		
		frame.setVisible(true);		
	}
	
	@After
	public void tearDown() {
		frame.setVisible(false);
		frame.dispose();
	}

	@Test
	public void testDrawPath() throws Exception {
		
		final RSPath path = new SwingPath();
		
		RSRect r = canvas.getClipBounds();
		
		path.moveTo(20, 20);
		
		path.lineTo(r.right - 20, 20);
		path.lineTo(r.right - 20, r.bottom - 20);
		path.moveTo(20, r.bottom - 20);
		path.lineTo(20, 20);
		
		final RSPaint paint = new SwingPaint();
		paint.setStyle(PaintStyle.STROKE);
		paint.setStrokeWidth(5);
		paint.setColor(uiLiaison.getRedColor());
		
		painter = new Painter() {
			
			@Override
			public void paint(SwingCanvas canvas) {				
				
				canvas.drawPath(path, paint);
			}
		};

		uiLiaison.repaint();
		
		Thread.sleep(5000);

	}
	
	@Test
	public void testDrawLine() throws Exception {
		
		painter = new Painter() {
			
			@Override
			public void paint(SwingCanvas canvas) {
				RSRect r = canvas.getClipBounds();
				r.left += 20;
				r.top += 20;
				r.right -= 20;
				r.bottom -= 20;
				
				int center = (r.bottom - r.top) / 2;
				
				RSPaint paint = new SwingPaint();
				paint.setStyle(PaintStyle.STROKE);
				paint.setStrokeWidth(5);
				paint.setColor(uiLiaison.getRedColor());
				
				canvas.drawLine(r.left, center , r.right, center, paint);
			}
		};

		uiLiaison.repaint();
		
		Thread.sleep(5000);

	}
	
	@Test
	public void testDrawRect() throws InterruptedException, InvocationTargetException {
		
		painter = new Painter() {
			
			@Override
			public void paint(SwingCanvas canvas) {
				RSRect r = canvas.getClipBounds();
				r.left += 20;
				r.top += 20;
				r.right -= 20;
				r.bottom -= 20;
				
				canvas.drawRect(r.left, r.top, r.right, r.bottom, paint);
			}
		};

		uiLiaison.repaint();
		
		Thread.sleep(5000);

	}
}
