package com.qq.client;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.qq.model.Jkfile;

/**
 * cell的样式Panel，实现ListCellIface
 * @ClassName TransferPanel
 * @author Jet
 * @date 2012-8-7
 */
public class CellPanel extends JPanel implements ListCellIface{

	private static final long serialVersionUID = 1L;
	private int index = 0;
	private CellPanel per2CellPanel=this;
	private BaseList baseList;
	private String bean;
	private JLabel label;
	private JLabel remove = new JLabel("下载点击");
	private CellPanel cp = null;
	public CellPanel(){
		super();
		inGui();
		cp = this;
	}
	private void inGui(){
		this.setMaximumSize(new Dimension(ScreenUtil.getScreeWidth(),40));
		this.setPreferredSize(new Dimension(0, 40));
		this.setOpaque(false);
		this.setLayout(null);
	}
	/**
	 * 实现接口中的方法
	 */
	@Override
	public JComponent getListCell(BaseList list, Object value) {
		final Jkfile file = (Jkfile)value;
		double num = (double)file.getFile().length();//(double)1024;
		DecimalFormat df = new DecimalFormat("0.00");
		String str = file.getFilename()+"("+df.format(num)+"KB) "+file.getUid();
		if(num > 1024) {
			num/=(double)1024;
			str = file.getFilename()+"("+df.format(num)+"KB) "+file.getUid();
		}
		this.bean = file.getFilename()+"("+df.format(num)+"KB) "+file.getUid();
		this.baseList=list;
		
		label = new JLabel(bean);
		label.setBounds(10, 0, 360, 30);
		remove.setBounds(315, 0, 100, 30);
		remove.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if(e.getButton()==MouseEvent.BUTTON1){
					
					File uploadFile = file.getFile();
					JFileChooser chooser = new JFileChooser();
					chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					File file = new File("G:/");
					chooser.setCurrentDirectory(file);
					int num = chooser.showSaveDialog(chooser);
					if(num == chooser.APPROVE_OPTION) {
						String path = chooser.getSelectedFile().toString()+"/"+uploadFile.getName();
						BufferedOutputStream bous = null;
						FileInputStream fins = null;
						try {
							bous = new BufferedOutputStream(new FileOutputStream(path));
							fins = new FileInputStream(uploadFile);
							byte[] data = new byte[1024];
							while(fins.read(data)!=-1) {
								bous.write(data);
							}
							bous.flush();
						} catch (FileNotFoundException e1) {
							e1.printStackTrace();
						} catch (IOException e3) {
							e3.printStackTrace();
						} finally{
							cp.remove(remove);
							remove = new JLabel("已下载");
							remove.setBounds(310, 0, 100, 30);
							cp.add(remove);
							cp.repaint();
							if(bous!=null) {
								try {
									bous.close();
								} catch (IOException e2) {
									e2.printStackTrace();
								}
							}
						}
					}
					
					
					
					
					
				}
			}
		});
		this.add(label);
		this.add(remove);
		return this;
	}
	@Override
	public void setSelect(boolean iss) {
		
	}
}
