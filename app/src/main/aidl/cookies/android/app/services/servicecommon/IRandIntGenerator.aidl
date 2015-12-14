package cookies.android.app.services.servicecommon;

import cookies.android.app.services.servicecommon.AidlServiceClientInfo;
import cookies.android.app.services.servicecommon.AidlServiceResult;

interface IRandIntGenerator {
    AidlServiceResult getRandomIntResult(in AidlServiceClientInfo clientInfo);
}