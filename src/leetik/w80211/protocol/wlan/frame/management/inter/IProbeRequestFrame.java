package leetik.w80211.protocol.wlan.frame.management.inter;

import java.util.List;

import leetik.w80211.protocol.wlan.frame.management.element.IWlanElement;

/**
 * Probe request management frame decoder<br/>
 * <ul>
 * <li>tagged parameters : X bytes</li>
 * </ul>
 * 
 * @author Bertrand Martel
 * 
 */
public interface IProbeRequestFrame {

	public List<IWlanElement> getTaggedParameter();
	
}
