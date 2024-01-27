package dev.prvt.yawiki.common.uuid;

import com.fasterxml.uuid.impl.UUIDUtil;
import java.util.UUID;

public class UuidUtil {

    public static byte[] asByteArray(UUID uuid) {
        return UUIDUtil.asByteArray(uuid);
    }

}
