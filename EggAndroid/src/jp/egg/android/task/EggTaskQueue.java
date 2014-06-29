package jp.egg.android.task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;

public class EggTaskQueue {

//	   /** The queue of requests to service. */
//		private final BlockingQueue<EggTask<?, ?>> mQueue;

	    /** The cache triage queue. */
	    private final PriorityBlockingQueue<EggTask<?,?>> mQueue =
	        new PriorityBlockingQueue<EggTask<?,?>>();



	    private boolean mIsRunning = false;
	    private ArrayList<ExecuteThread> mThreads = new ArrayList<ExecuteThread>();


		public EggTaskQueue() {

		}


		public EggTask<?, ?> take() throws InterruptedException{
			EggTask<?, ?> task = mQueue.take();
			return  task;
		}


		public void add(EggTask<?, ?> task){
			task.setSequence();
			task.start();
			mQueue.put(task);
		}



		public void start(){
			if(mIsRunning) return ;
			mIsRunning = true;

			int num_thread = 4;
			for(int i=0;i<num_thread;i++){
				ExecuteThread t = new ExecuteThread(this);
				mThreads.add(t);
				t.start();
			}

		}

		public void stop(){
			if(!mIsRunning) return ;
			mIsRunning = false;

			List<ExecuteThread> ts = mThreads;
			for(int i=0;i<ts.size();i++){
				ExecuteThread t = ts.get(i);
				t.quit();
				ts.remove(t);
			}

		}

}
