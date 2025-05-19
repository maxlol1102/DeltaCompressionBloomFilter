package sync;

import java.util.List;

public class VersionManager {
    public static boolean syncToTargetVersion(
        VersionedFilter local,
        VersionedFilter remote,
        List<byte[]> deltasFromRemote
    ) {
        for (byte[] delta : deltasFromRemote) {
            boolean success = local.applyDelta(delta);
            if (!success) {
                System.out.println("Failed to apply delta. Full sync may be needed.");
                return false;
            }
        }

        if (local.getVersion() != remote.getVersion()) {
            System.out.println("Warning: version mismatch after sync.");
        }

        return true;
    }
}
