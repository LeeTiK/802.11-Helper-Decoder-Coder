/**
 * The MIT License (MIT)
 * 
 * Copyright (c) 2015 Bertrand Martel
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package leetik.w80211.protocol.wlan;

import leetik.w80211.protocol.wlan.constant.WlanFrameSubType;
import leetik.w80211.protocol.wlan.constant.WlanFrameType;
import leetik.w80211.protocol.wlan.frame.IWlanFrame;
import leetik.w80211.protocol.wlan.frame.control.*;
import leetik.w80211.protocol.wlan.frame.data.DataFrame;
import leetik.w80211.protocol.wlan.frame.data.NullFrame;
import leetik.w80211.protocol.wlan.frame.data.QosDataFrame;
import leetik.w80211.protocol.wlan.frame.management.AssociationRequestFrame;
import leetik.w80211.protocol.wlan.frame.management.AuthenticationFrame;
import leetik.w80211.protocol.wlan.frame.management.BeaconFrame;
import leetik.w80211.protocol.wlan.frame.management.DeAuthenticationFrame;
import leetik.w80211.protocol.wlan.frame.management.DisassociationFrame;
import leetik.w80211.protocol.wlan.frame.management.IbssAnnouncementIndicationMapFrame;
import leetik.w80211.protocol.wlan.frame.management.ProbeRequestFrame;
import leetik.w80211.protocol.wlan.frame.management.ProbeResponseFrame;
import leetik.w80211.protocol.wlan.frame.management.ReAssociationResponseFrame;
import leetik.w80211.protocol.wlan.frame.management.ReassociationRequestFrame;
import leetik.w80211.protocol.wlan.inter.IWlanFrameControl;

import java.nio.ByteBuffer;

/**
 * Decode 802.11 Wlan frame<br/>
 * http://technet.microsoft.com/en-us/library/cc757419(v=ws.10).aspx<br/>
 * http://inst.eecs.berkeley.edu/~ee122/sp07/80211.pdf
 * 
 * @author Bertrand Martel
 * 
 */
public class WlanFramePacket {

	/**
	 * contains control information used for defining the type of 802.11 MAC
	 * frame and providing information
	 */
	WlanPacket wlanPacket;

	private IWlanFrameControl frameControl = null;

	private IWlanFrame wlanFrame = null;

	private byte[] fcs = new byte[]{};

	/**
	 * Decode frame extracted from byte array (byte array is complete w80211
	 * frames)
	 * 
	 * @param frame
	 *            byte array data
	 */
	@Deprecated
	public WlanFramePacket(byte[] frame) {

		frameControl = new WlanFrameControl(new byte[] { frame[0], frame[1] });

		// define new frame shifted to the right
		byte[] wlanFrameData = new byte[frame.length - 2];
		System.arraycopy(frame, 2, wlanFrameData, 0, frame.length - 2);

		switch (frameControl.getType()) {
		case WlanFrameType.MANAGEMENT_FRAME_TYPE:

			switch (frameControl.getSubType()) {

			case WlanFrameSubType.MANAGEMENT_ASSOCIATION_REQUEST_FRAME:
				wlanFrame = new AssociationRequestFrame(wlanFrameData);
				break;
			case WlanFrameSubType.MANAGEMENT_REASSOCIATION_REQUEST_FRAME:
				wlanFrame = new ReassociationRequestFrame(wlanFrameData);
				break;
			case WlanFrameSubType.MANAGEMENT_REASSOCIATION_RESPONSE_FRAME:
				wlanFrame = new ReAssociationResponseFrame(wlanFrameData);
				break;
			case WlanFrameSubType.MANAGEMENT_PROBE_REQUEST_FRAME:
				wlanFrame = new ProbeRequestFrame(wlanFrameData);
				break;
			case WlanFrameSubType.MANAGEMENT_PROBE_RESPONSE_FRAME:
				wlanFrame = new ProbeResponseFrame(wlanFrameData);
				break;
			case WlanFrameSubType.MANAGEMENT_BEACON_FRAME:
				wlanFrame = new BeaconFrame(wlanFrameData);
				break;
			case WlanFrameSubType.MANAGEMENT_ANNOUNCEMENT_TRAFFIC_INDICATION_MESSAGE_FRAME:
				wlanFrame = new IbssAnnouncementIndicationMapFrame(wlanFrameData);
				break;
			case WlanFrameSubType.MANAGEMENT_DISASSOCIATION_FRAME:
				wlanFrame = new DisassociationFrame(wlanFrameData);
				break;
			case WlanFrameSubType.MANAGEMENT_AUTHENTICATION_FRAME:
				wlanFrame = new AuthenticationFrame(wlanFrameData);
				break;
			case WlanFrameSubType.MANAGEMENT_DEAUTHENTICATION_FRAME:
				wlanFrame = new DeAuthenticationFrame(wlanFrameData);
				break;
			}
			break;

		case WlanFrameType.CONTROL_FRAME_TYPE:

			switch (frameControl.getSubType()) {
			case WlanFrameSubType.CONTROL_POWER_SAVE_POLLING_PACKET:
				wlanFrame = new PowerSavePollingFrame(wlanFrameData);
				break;
			case WlanFrameSubType.CONTROL_REQUEST_TO_SEND:
				wlanFrame = new RequestToSendFrame(wlanFrameData);
				break;
			case WlanFrameSubType.CONTROL_CLEAR_TO_SEND:
				wlanFrame = new ClearToSendFrame(wlanFrameData);
				break;
			case WlanFrameSubType.CONTROL_ACK:
				wlanFrame = new AckFrame(wlanFrameData);
				break;
			case WlanFrameSubType.CONTROL_SIGNAL_CONTENTION_FREE:
				wlanFrame = new ContentionFreeFrame(wlanFrameData);
				break;
			case WlanFrameSubType.CONTROL_SIGNAL_CONTENTION_FREE_AND_RECEIVE_ACK:
				wlanFrame = new ContentionFreeReceiveAckFrame(wlanFrameData);
				break;
			}
			break;
		case WlanFrameType.DATA_FRAME_TYPE:
			switch (frameControl.getSubType()) {
			case WlanFrameSubType.DATA_FRAME:
				wlanFrame = new DataFrame(wlanFrameData, frameControl.isToDS(),
						frameControl.isFromDS());
				break;
			case WlanFrameSubType.DATA_CONTENTION_FREE_ACK:
				wlanFrame = new DataFrame(wlanFrameData, frameControl.isToDS(),
						frameControl.isFromDS());
				break;
			case WlanFrameSubType.DATA_CONTENTION_FREE_POLL:
				wlanFrame = new DataFrame(wlanFrameData, frameControl.isToDS(),
						frameControl.isFromDS());
				break;
			case WlanFrameSubType.DATA_CONTENTION_FREE_ACK_PLUS_POLL:
				wlanFrame = new DataFrame(wlanFrameData, frameControl.isToDS(),
						frameControl.isFromDS());
				break;
			case WlanFrameSubType.DATA_NULL_FRAME:
				wlanFrame = new NullFrame(wlanFrameData, frameControl.isToDS(),
						frameControl.isFromDS());
				break;
			case WlanFrameSubType.CONTENTION_FREE_ACK:
				wlanFrame = new DataFrame(wlanFrameData, frameControl.isToDS(),
						frameControl.isFromDS());
				break;
			case WlanFrameSubType.CONTENTION_FREE_POLL:
				wlanFrame = new DataFrame(wlanFrameData, frameControl.isToDS(),
						frameControl.isFromDS());
				break;
			case WlanFrameSubType.CONTENTION_FREE_ACK_PLUS_POLL:
				wlanFrame = new DataFrame(wlanFrameData, frameControl.isToDS(),
						frameControl.isFromDS());
				break;
			case WlanFrameSubType.DATA_QOS_FRAME:
				wlanFrame = new QosDataFrame(wlanFrameData,
						frameControl.isToDS(), frameControl.isFromDS());
				break;
			case WlanFrameSubType.DATA_QOS_CONTENTION_FREE_ACK_FRAME:
				wlanFrame = new QosDataFrame(wlanFrameData,
						frameControl.isToDS(), frameControl.isFromDS());
				break;
			case WlanFrameSubType.DATA_QOS_CONTENTION_FREE_POLL_FRAME:
				wlanFrame = new QosDataFrame(wlanFrameData,
						frameControl.isToDS(), frameControl.isFromDS());
				break;
			case WlanFrameSubType.DATA_QOS_CONTENTION_FREE_ACK_PLUS_POLL_FRAME:
				wlanFrame = new QosDataFrame(wlanFrameData,
						frameControl.isToDS(), frameControl.isFromDS());
				break;
			case WlanFrameSubType.DATA_QOS_NULL_FRAME:
				wlanFrame = new NullFrame(wlanFrameData, frameControl.isToDS(),
						frameControl.isFromDS());
				break;
			case WlanFrameSubType.QOS_CONTENTION_FREE_ACK_FRAME:
				wlanFrame = new QosDataFrame(wlanFrameData,
						frameControl.isToDS(), frameControl.isFromDS());
				break;
			case WlanFrameSubType.QOS_CONTENTION_FREE_POLL_FRAME:
				wlanFrame = new QosDataFrame(wlanFrameData,
						frameControl.isToDS(), frameControl.isFromDS());
				break;
			case WlanFrameSubType.QOS_CONTENTION_FREE_ACK_PLUS_POLL_FRAME:
				wlanFrame = new QosDataFrame(wlanFrameData,
						frameControl.isToDS(), frameControl.isFromDS());
				break;
			}
			break;
		}
	}

	public WlanFramePacket(ByteBuffer byteBuffer, WlanPacket wlanPacket) {

		this.wlanPacket = wlanPacket;

		frameControl = new WlanFrameControl(byteBuffer.getShort());

		boolean fcs = false;

		if (wlanPacket.getRadioTap().getRadioTapDataLength()>0)
		{
			fcs = wlanPacket.getRadioTap().getRadioTapData().getFlags().isFCSatEnd();
		}

		if (fcs)
		{
			this.fcs = new byte[4];
			byteBuffer.mark();
			byteBuffer.position(byteBuffer.limit()-4);

			byteBuffer.get(this.fcs);

			byteBuffer.limit(byteBuffer.limit()-4);
			byteBuffer.reset();
		}

		// define new frame shifted to the right
		//byte[] wlanFrameData = new byte[frame.remaining()];
		//frame.get(wlanFrameData);

		switch (frameControl.getType()) {
			case WlanFrameType.MANAGEMENT_FRAME_TYPE:

				switch (frameControl.getSubType()) {

					case WlanFrameSubType.MANAGEMENT_ASSOCIATION_REQUEST_FRAME:
						wlanFrame = new AssociationRequestFrame(byteBuffer);
						break;
					case WlanFrameSubType.MANAGEMENT_REASSOCIATION_REQUEST_FRAME:
						wlanFrame = new ReassociationRequestFrame(byteBuffer);
						break;
					case WlanFrameSubType.MANAGEMENT_REASSOCIATION_RESPONSE_FRAME:
						wlanFrame = new ReAssociationResponseFrame(byteBuffer);
						break;
					case WlanFrameSubType.MANAGEMENT_PROBE_REQUEST_FRAME:
						wlanFrame = new ProbeRequestFrame(byteBuffer);
						break;
					case WlanFrameSubType.MANAGEMENT_PROBE_RESPONSE_FRAME:
						wlanFrame = new ProbeResponseFrame(byteBuffer);
						break;
					case WlanFrameSubType.MANAGEMENT_BEACON_FRAME:
						wlanFrame = new BeaconFrame(byteBuffer);
						break;
					case WlanFrameSubType.MANAGEMENT_ANNOUNCEMENT_TRAFFIC_INDICATION_MESSAGE_FRAME:
						wlanFrame = new IbssAnnouncementIndicationMapFrame(byteBuffer);
						break;
					case WlanFrameSubType.MANAGEMENT_DISASSOCIATION_FRAME:
						wlanFrame = new DisassociationFrame(byteBuffer);
						break;
					case WlanFrameSubType.MANAGEMENT_AUTHENTICATION_FRAME:
						wlanFrame = new AuthenticationFrame(byteBuffer);
						break;
					case WlanFrameSubType.MANAGEMENT_DEAUTHENTICATION_FRAME:
						wlanFrame = new DeAuthenticationFrame(byteBuffer);
						break;
				}
				break;

			case WlanFrameType.CONTROL_FRAME_TYPE:

				switch (frameControl.getSubType()) {
					case WlanFrameSubType.CONTROL_BLOCK_ACK_REQ:
						wlanFrame = new BlockAckReq(byteBuffer);
						break;
					case WlanFrameSubType.CONTROL_BLOCK_ACK:
						wlanFrame = new BlockAck(byteBuffer);
						break;
					case WlanFrameSubType.CONTROL_POWER_SAVE_POLLING_PACKET:
						wlanFrame = new PowerSavePollingFrame(byteBuffer);
						break;
					case WlanFrameSubType.CONTROL_REQUEST_TO_SEND:
						wlanFrame = new RequestToSendFrame(byteBuffer);
						break;
					case WlanFrameSubType.CONTROL_CLEAR_TO_SEND:
						wlanFrame = new ClearToSendFrame(byteBuffer);
						break;
					case WlanFrameSubType.CONTROL_ACK:
						wlanFrame = new AckFrame(byteBuffer);
						break;
					case WlanFrameSubType.CONTROL_SIGNAL_CONTENTION_FREE:
						wlanFrame = new ContentionFreeFrame(byteBuffer);
						break;
					case WlanFrameSubType.CONTROL_SIGNAL_CONTENTION_FREE_AND_RECEIVE_ACK:
						wlanFrame = new ContentionFreeReceiveAckFrame(byteBuffer);
						break;
				}
				break;
			case WlanFrameType.DATA_FRAME_TYPE:
				switch (frameControl.getSubType()) {
					case WlanFrameSubType.DATA_FRAME:
						wlanFrame = new DataFrame(byteBuffer, this);
						break;
					case WlanFrameSubType.DATA_CONTENTION_FREE_ACK:
						wlanFrame = new DataFrame(byteBuffer, this);
						break;
					case WlanFrameSubType.DATA_CONTENTION_FREE_POLL:
						wlanFrame = new DataFrame(byteBuffer, this);
						break;
					case WlanFrameSubType.DATA_CONTENTION_FREE_ACK_PLUS_POLL:
						wlanFrame = new DataFrame(byteBuffer, this);
						break;
					case WlanFrameSubType.DATA_NULL_FRAME:
						wlanFrame = new NullFrame(byteBuffer, this);
						break;
					case WlanFrameSubType.CONTENTION_FREE_ACK:
						wlanFrame = new DataFrame(byteBuffer, this);
						break;
					case WlanFrameSubType.CONTENTION_FREE_POLL:
						wlanFrame = new DataFrame(byteBuffer,this);
						break;
					case WlanFrameSubType.CONTENTION_FREE_ACK_PLUS_POLL:
						wlanFrame = new DataFrame(byteBuffer,this);
						break;
					case WlanFrameSubType.DATA_QOS_FRAME:
						wlanFrame = new QosDataFrame(byteBuffer, this);
						break;
					case WlanFrameSubType.DATA_QOS_CONTENTION_FREE_ACK_FRAME:
						wlanFrame = new QosDataFrame(byteBuffer, this);
						break;
					case WlanFrameSubType.DATA_QOS_CONTENTION_FREE_POLL_FRAME:
						wlanFrame = new QosDataFrame(byteBuffer,this);
						break;
					case WlanFrameSubType.DATA_QOS_CONTENTION_FREE_ACK_PLUS_POLL_FRAME:
						wlanFrame = new QosDataFrame(byteBuffer,this);
						break;
					case WlanFrameSubType.DATA_QOS_NULL_FRAME:
						wlanFrame = new NullFrame(byteBuffer, this);
						break;
					case WlanFrameSubType.QOS_CONTENTION_FREE_ACK_FRAME:
						wlanFrame = new QosDataFrame(byteBuffer,this);
						break;
					case WlanFrameSubType.QOS_CONTENTION_FREE_POLL_FRAME:
						wlanFrame = new QosDataFrame(byteBuffer,this);
						break;
					case WlanFrameSubType.QOS_CONTENTION_FREE_ACK_PLUS_POLL_FRAME:
						wlanFrame = new QosDataFrame(byteBuffer,this);
						break;
				}
				break;
		}
	}

	public IWlanFrameControl getFrameControl() {
		return frameControl;
	}

	public IWlanFrame getWlanFrame() {
		return wlanFrame;
	}

	public WlanPacket getWlanPacket() {
		return wlanPacket;
	}
}
