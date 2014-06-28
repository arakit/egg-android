package jp.egg.android.task;

import java.util.ArrayList;
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

			for(int i=0;i<1;i++){
				ExecuteThread t = new ExecuteThread(this);
				mThreads.add(t);
				t.start();
			}

		}

		public void stop(){
			if(!mIsRunning) return ;
			mIsRunning = false;

			for(int i=0;i<mThreads.size();i++){
				ExecuteThread t = mThreads.get(i);
				t.quit();
				mThreads.remove(t);
			}

		}

}
