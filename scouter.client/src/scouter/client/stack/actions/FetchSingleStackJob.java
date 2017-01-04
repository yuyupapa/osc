package scouter.client.stack.actions;

import java.io.IOException;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import scouter.client.net.INetReader;
import scouter.client.net.TcpProxy;
import scouter.client.util.ExUtil;
import scouter.client.views.ObjectThreadDumpView;
import scouter.io.DataInputX;
import scouter.lang.pack.MapPack;
import scouter.lang.pack.StackPack;
import scouter.net.RequestCmd;

public class FetchSingleStackJob extends Job {

	int serverId;
	String objName;
	long time;
	List<Long> list;
	ObjectThreadDumpView view;
	
	public FetchSingleStackJob(int serverId, String objName, long time, List<Long> list, ObjectThreadDumpView view) {
		super(objName + " stack ...");
		this.serverId = serverId;
		this.objName = objName;
		this.time = time;
		this.list = list;
		this.view = view;
	}

	protected IStatus run(final IProgressMonitor monitor) {
		monitor.beginTask("Fetching...", 1);
		TcpProxy tcp = TcpProxy.getTcpProxy(serverId);
		final StringBuilder content = new StringBuilder();
		try {
			MapPack param = new MapPack();
			param.put("objName", objName);
			param.put("from", time);
			param.put("to", time + 1);
			tcp.process(RequestCmd.GET_STACK_ANALYZER, param, new INetReader() {
				int count = 0;
				public void process(DataInputX in) throws IOException {
					StackPack sp = (StackPack) in.readPack();
					content.append(sp.getStack());
					monitor.worked(count++);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			TcpProxy.putTcpProxy(tcp);
		}
		
		monitor.done();
		ExUtil.exec(Display.getDefault(), new Runnable() {
			public void run() {
		    	IWorkbench workbench = PlatformUI.getWorkbench();
		    	IWorkbenchWindow window = workbench.getActiveWorkbenchWindow(); 				
				if (window != null) {
					try {
						if(view == null){
							ObjectThreadDumpView newView = (ObjectThreadDumpView) window.getActivePage().showView(ObjectThreadDumpView.ID, null, IWorkbenchPage.VIEW_ACTIVATE);
							if (newView != null) {
								newView.setInput(content.toString(), objName, serverId, time, list);
							}
						}else{
							view.setInput(content.toString());
						}
					} catch (PartInitException e) {
						MessageDialog.openError(window.getShell(), "Error", "Error opening view:" + e.getMessage());
					}
				}
			}
		});
		
		return Status.OK_STATUS;
	}
}
