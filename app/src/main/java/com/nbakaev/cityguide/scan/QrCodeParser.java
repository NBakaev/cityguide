package com.nbakaev.cityguide.scan;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Nikita Bakaev on 12/27/2016.
 */

public class QrCodeParser {

    public boolean isOurQrCode(String lScannedCode) {
        String teml = "(https://cityguide.nbakaev.com/#/poi/).+";
        Pattern pattern = Pattern.compile(teml);
        Matcher matcher = pattern.matcher(lScannedCode);
        return matcher.matches();
    }

    public String getPoiFromUrl(String Url) {
        String templ = "(https://cityguide.nbakaev.com/#/poi/)";
        return Url.split(templ)[1];
    }

}
