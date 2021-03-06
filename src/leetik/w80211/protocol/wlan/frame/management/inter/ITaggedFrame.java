package leetik.w80211.protocol.wlan.frame.management.inter;

import leetik.w80211.protocol.wlan.frame.management.element.EWlanElementID;
import leetik.w80211.protocol.wlan.frame.management.element.IWlanElement;
import leetik.w80211.protocol.wlan.frame.management.element.WlanElementAbstr;
import leetik.w80211.protocol.wlan.frame.management.element.impl.VendorSpecificElement;
import leetik.w80211.protocol.wlan.frame.management.element.impl.vendorSpecific.EVendorSpecificElementType;

import java.util.ArrayList;
import java.util.List;

public interface ITaggedFrame {

    public List<IWlanElement> getTaggedParameter();

    public WlanElementAbstr getTaggedParameter(EWlanElementID EWlanElementID);

    public VendorSpecificElement getTaggedParameterVendorSpecific(EVendorSpecificElementType vendorSpecificElementType);
}
