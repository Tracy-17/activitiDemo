import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Test;

/**
 * Author:ShiQi
 * Date:2021/11/15-3:34
 */
public class Test2 {
    /**
     * 启动流程实例，添加businessKey
     */
    @Test
    public void test1(){
        ProcessEngine engine= ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService=engine.getRuntimeService();
        ProcessInstance instance = runtimeService.startProcessInstanceByKey("runGame", "1");
        System.out.println("businessKey="+instance.getBusinessKey());
    }
    /**
     *全部流程挂起
     */
    @Test
    public void test2(){
        ProcessEngine engine= ProcessEngines.getDefaultProcessEngine();
        RepositoryService service=engine.getRepositoryService();
        //查询流程定义的对象
        ProcessDefinition processDefinition=service.createProcessDefinitionQuery()
                .processDefinitionKey("runGame")
                .latestVersion()
                .singleResult();
        //获取当前流程定义的状态，挂起-->激活，激活-->挂起
        boolean suspended=processDefinition.isSuspended();
        String id=processDefinition.getId();
        if(suspended){
            //挂起（暂停）:流程定义ID，是否激活，激活时间
            service.activateProcessDefinitionById(id,true,null);
            System.out.println("流程："+id+" 已激活");
        }else{
            service.suspendProcessDefinitionById(id,true,null);
            System.out.println("流程："+id+" 已挂起");
        }
    }
    /**
     * 单个流程挂起（挂起的实例执行会抛异常）
     */
    @Test
    public void test3(){
        ProcessEngine engine= ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService=engine.getRuntimeService();
        ProcessInstance processInstance=runtimeService.createProcessInstanceQuery()
                .processInstanceId("45001")
                .singleResult();
        boolean suspended=processInstance.isSuspended();
        String id=processInstance.getId();
        if(suspended){
            runtimeService.activateProcessInstanceById(id);
            System.out.println("流程："+id+" 已激活");
        }else{
            runtimeService.suspendProcessInstanceById(id);
            System.out.println("流程："+id+" 已挂起");
        }
    }
}
