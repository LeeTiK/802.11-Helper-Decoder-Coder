package leetik.w80211.protocol.wlan.frame.management;

import leetik.w80211.protocol.wlan.frame.WlanManagementAbstr;
import leetik.w80211.protocol.wlan.frame.management.inter.IDisassociationFrame;
import leetik.w80211.protocol.wlan.utils.ByteUtils;

import java.nio.ByteBuffer;

/**
 * Management frame - Disassociation frame<br/>
 * <ul>
 * <li>status code :2 Bytes</li>
 * </ul>
 * <p>
 * contains only fixed parameters
 * </p>
 * 
 * @author Bertrand Martel
 * 
 */
public class DisassociationFrame extends WlanManagementAbstr implements IDisassociationFrame {

	/**
	 * authentication status code
	 */
	private int statusCode = 0;

	/**
	 * Parse Disassociation management frame according to basic management frame
	 * and Disassociation frame specification
	 * 
	 * @param frame
	 *            frame with omitted control frame
	 */
	@Deprecated
	public DisassociationFrame(byte[] frame) {
		super(frame);
		byte[] frameBody = getFrameBody();
		statusCode = ByteUtils.convertByteArrayToInt(new byte[] {
				frameBody[1], frameBody[0] });
	}

	public DisassociationFrame(ByteBuffer frame) {
		super(frame);
		byte[] frameBody = getFrameBody();
		statusCode = ByteUtils.convertByteArrayToInt(new byte[] {
				frameBody[1], frameBody[0] });
	}

	@Override
	public int getStatusCode() {
		return statusCode;
	}

}
