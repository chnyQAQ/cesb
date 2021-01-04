package com.dah.desb.infrastructure.worker;

import java.util.Map;

public interface WorkerCallback {

	void doInWorker(Map<Object, Object> context);

}
