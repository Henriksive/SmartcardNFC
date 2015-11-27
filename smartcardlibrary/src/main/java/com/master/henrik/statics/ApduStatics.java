package com.master.henrik.statics;

import com.master.henrik.shared.Converter;

/**
 * Created by Henri on 27.11.2015.
 */
public class ApduStatics {
    public final static double extendedAPDULength = 32764.00;
    public final static double shortAPDULenght = 255.00;
    public final static byte[] apduSignature = Converter.HexStringToByteArray("75983FDE41AD5C75568175718266D159");
}
