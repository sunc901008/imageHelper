import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;


public class Test extends JFrame {

    private String zoomImage(String source, float quality) {
        File srcFile = new File(source);
        int index = source.lastIndexOf(".");
        String fileType = source.substring(index + 1);
        String dest = source.substring(0, index) + "_" + Calendar.getInstance().getTimeInMillis() + source.substring(index);
        File destFile = new File(dest);
        try {
            BufferedImage bufImg = ImageIO.read(srcFile);
            int w = new Float(bufImg.getWidth() * quality).intValue();
            int h = new Float(bufImg.getHeight() * quality).intValue();
            bufImg.getScaledInstance(w, h, Image.SCALE_SMOOTH);
            AffineTransformOp ato = new AffineTransformOp(AffineTransform.getScaleInstance(quality, quality), null);
            Image Itemp = ato.filter(bufImg, null);
            ImageIO.write((BufferedImage) Itemp, fileType, destFile);
            return dest;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private String writeHighQuality(String source, float quality) {
        String dest = null;
        try {
            File sourceFile = new File(source);
            int index = source.lastIndexOf(".");
            BufferedImage im = ImageIO.read(sourceFile);
            dest = source.substring(0, index) + "_" + Calendar.getInstance().getTimeInMillis() + source.substring(index);
            FileOutputStream newImage = new FileOutputStream(dest);
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(newImage);

            JPEGEncodeParam jep = JPEGCodec.getDefaultJPEGEncodeParam(im);
                /* 压缩质量 */
            jep.setQuality(quality, true);
            encoder.encode(im, jep);
            newImage.close();
            return dest;
        } catch (Exception e) {
            e.printStackTrace();
            return dest;
        }
    }

    private boolean isEmpty(String str) {
        return str == null || "".equals(str);
    }

    private static final List<String> types = Arrays.asList("jpg", "jpeg", "bmp", "git", "png");

    private boolean invalidType(String filePath) {
        String type = filePath.substring(filePath.lastIndexOf(".") + 1).toLowerCase();
        return types.contains(type);
    }

    private Rectangle get(int x, int y, int with) {
        return new Rectangle(x, y, with, 20);
    }

    private void start() {
        setTitle("叫大哥!");
        setLayout(null);
        setBounds(500, 200, 700, 360);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//        setLocationRelativeTo(null);
        setResizable(false);

        int x1 = 10;
        int x2 = 135;
        int y1 = 10;
        int withLabel = 120;
        int withText = 300;
        int withButton = 80;
        JLabel selectLabel = new JLabel("文件路径:", SwingConstants.RIGHT);
        selectLabel.setBounds(get(x1, y1, withLabel));
        JTextField selectText = new JTextField(200);
        selectText.setBounds(get(x2, y1, withText));
        selectText.setEditable(false);
        JButton jbFile = new JButton("选择");
        jbFile.setBounds(get(440, y1, withButton));
        JButton jbClear = new JButton("清除");
        jbClear.setBounds(get(530, y1, withButton));

        int y2 = 60;
        JLabel radioLabel = new JLabel("压缩质量(0.0-1.0):", SwingConstants.RIGHT);
        radioLabel.setBounds(get(x1, y2, withLabel));
        JTextField radioText = new JTextField(5);
        radioText.setBounds(get(x2, y2, withText));
        radioText.setText("0.9");

        int y3 = 110;
        JLabel boxLabel = new JLabel("无损压缩:", SwingConstants.RIGHT);
        boxLabel.setBounds(get(x1, y3, withLabel));

        JCheckBox jc = new JCheckBox();
        jc.setBounds(get(130, y3, 20));
        jc.setSelected(true);

        JButton jbWrite = new JButton("压缩");
        jbWrite.setBounds(get(240, y3, withButton));

        int y4 = 160;
        JLabel destLabel = new JLabel("保存位置:", SwingConstants.RIGHT);
        destLabel.setBounds(get(x1, y4, withLabel));
        JTextField destText = new JTextField(200);
        destText.setBounds(get(x2, y4, withText));
        destText.setEditable(false);

        JButton jbKid = new JButton("别点我!");
        Rectangle rect = get(240, 240, withLabel);
        jbKid.setBounds(rect);

        JLabel tips = new JLabel("", SwingConstants.CENTER);
        tips.setBounds(get(240, 200, withLabel));

        add(jbFile);
        add(jbClear);
        add(selectLabel);
        add(selectText);
        add(radioLabel);
        add(radioText);
        add(boxLabel);
        add(jc);
        add(jbWrite);
        add(destLabel);
        add(destText);
        add(tips);
        add(jbKid);

        List<String> pathSelected = new ArrayList<>();
        jbFile.addActionListener(e -> {
            tips.setText("");
            JFileChooser jfc = new JFileChooser();
            jfc.setFileFilter(new FileNameExtensionFilter("图片文件", "jpg", "jpeg", "bmp", "git", "png"));
            if (jfc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                selectText.setText(jfc.getSelectedFile().getAbsolutePath());
                pathSelected.clear();
                pathSelected.add(jfc.getSelectedFile().getAbsolutePath());
            }
        });

        jbClear.addActionListener(e -> {
            selectText.setText("");
            destText.setText("");
            pathSelected.clear();
            tips.setText("");
        });

        jbWrite.addActionListener(e -> {
            tips.setForeground(Color.RED);
            tips.setText("");
            String path = pathSelected.size() > 0 ? pathSelected.get(0) : null;
            if (isEmpty(path)) {
                tips.setText("请选择文件.");
            } else if (!invalidType(path)) {
                tips.setText("别调皮,选择图片文件.");
            } else {
                String quality = radioText.getText();
                float q;
                if (isEmpty(quality)) {
                    radioText.setText("0.9");
                    q = 0.9f;
                } else {
                    q = Float.parseFloat(quality);
                }
                if (q <= 0 || q > 1.0) {
                    radioText.setText("0.9");
                    q = 0.9f;
                }
                String dest;
                if (jc.isSelected()) {
                    dest = writeHighQuality(path, q);
                } else {
                    dest = zoomImage(path, q);
                }
                if (isEmpty(dest)) {
                    tips.setText("未知错误.");
                } else {
                    destText.setText(dest);
                    tips.setForeground(Color.GREEN);
                    tips.setText("压缩成功.");
                }
            }
        });

        jbKid.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                jbKid.setText("别点我!");
                jbKid.setLocation(jbKid.getX() + e.getX() + 10, jbKid.getY() + e.getY() + 10);
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });

        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (Math.abs(rect.getX() - jbKid.getX()) >= 10 || Math.abs(rect.getY() - jbKid.getY()) >= 10) {
                    jbKid.setText("真淘气!");
                    jbKid.setBounds(rect);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        setVisible(true);
    }

    public static void main(String args[]) {
        Test t = new Test();
        t.start();
    }
}