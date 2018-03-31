package android.com.main;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

import javax.imageio.ImageIO;

public class ImageUtil {
	/**
	 *±N¹Ï§ÎÁY¤p«á¦^¶Ç¡A¦pªGµo¥Í¿ù»~´Nª½±µ¦^¶Ç­ì¹Ï¡A¨Ò¦p:imageSize<=1¡BµLªk¨ú±o¹Ïªº¼e°ª¡Bµo¥Íexception
	 * 
	 * @param srcImageData
	 *           ¨Ó·½¹Ï§Î¸ê®Æ
	 * @param scaleSize
	 *            ±ý±N¹Ï§ÎÁY¦Ü¦h¤Ö¤Ø¤o¥H¤U¡A50¥Nªí±N¤Ø¤oÁY¤p¦Ü50x50¥H¤º…§
	 * @return ÁY¤p§¹²¦ªº¹Ï§Î¸ê®Æ«¢«¢
	 */
	public static byte[] shrink(byte[] srcImageData, int scaleSize) {
		ByteArrayInputStream bais = new ByteArrayInputStream(srcImageData);
		// ÁY¤p¤ñ¨Ò¡A4¥Nªí°£¥H4
		int sampleSize = 1;
		int imageWidth = 0;
		int imageHeight = 0;

		// ¦pªGimageSize¬°0¡A­t¼Æ(¥Nªí¿ù»~)©Î1(1¥Nªí»P­ì¹Ï¤@¼Ë¤j¤p)¡A´Nª½±µ¦^¶Ç­ì¹Ï¸ê®Æ
		if (scaleSize <= 1) {
			return srcImageData;
		}

		try {
			BufferedImage srcBufferedImage = ImageIO.read(bais);
			//¦pªGµLªk¿ë§O¹ÏÀÉ®æ¦¡(TYPE_CUSTOM)´N¦^¶ÇTYPE_INT_ARGB;§_«h¦^¶Ç¬J¦³®æ¦¡
			int type = srcBufferedImage.getType() == BufferedImage.TYPE_CUSTOM ? BufferedImage.TYPE_INT_RGB
					: srcBufferedImage.getType();
			imageWidth = srcBufferedImage.getWidth();
			imageHeight = srcBufferedImage.getHeight();
			if (imageWidth == 0 || imageHeight == 0) {
				return srcImageData;
			}
			//¥u­n¹ÏÀÉ¸ûªøªº¤@Ãä¶W¹L«ü©wªø«×(desireSize)¡A´N­pºâ±ýÁY¤pªº¤ñ¨Ò
			//¨Ã±N¹ÏÀÉ¼e°ª³£°£¥H±ýÁY¤pªº¤ñ¨Ò
			int longer = Math.max(imageWidth, imageHeight);
			if (longer > scaleSize) {
				sampleSize = longer / scaleSize;
				imageWidth = srcBufferedImage.getWidth() / sampleSize;
				imageHeight = srcBufferedImage.getHeight() / sampleSize;
			}
			BufferedImage scaledBufferedImage = new BufferedImage(imageWidth,
					imageHeight, type);
			Graphics graphics = scaledBufferedImage.createGraphics();
			graphics.drawImage(srcBufferedImage, 0, 0, imageWidth, imageHeight,
					null);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(scaledBufferedImage, "jpg", baos);
			return baos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			return srcImageData;
		}
	}
}
