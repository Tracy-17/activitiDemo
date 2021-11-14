import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricActivityInstanceQuery;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.SQLOutput;
import java.util.List;


/**
 * Author:ShiQi
 * Date:2021/11/14-18:57
 */
public class Test1{
/*
*1.生成activiti相关的表结构
 */
    @Test
    public void test1(){
        //使用classpath下的activiti.cfg.xml中的配置来创建ProcessEngine对象
        ProcessEngine engine= ProcessEngines.getDefaultProcessEngine();
        System.out.println(engine);

    }
/*
* 2.自定义的方式加载配置文件
* */
    @Test
    public void test2(){
        //创建
        ProcessEngineConfiguration configuration=ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml");
        //通过configuration创建ProcessEngine对象
        ProcessEngine processEngine=configuration.buildProcessEngine();
    }
    /*
    * 实现文件的单个部署
    * */
    @Test
    public void test3(){
        ProcessEngine engine=ProcessEngines.getDefaultProcessEngine();
        RepositoryService service=engine.getRepositoryService();
        Deployment deploy= (Deployment) service.createDeployment()
                .addClasspathResource("diagrams/run.bpmn")
//                .addClasspathResource("run.png")
                .name("runGame")
                .deploy();
        System.out.println("流程ID:"+deploy.getId()+ deploy.getName());
    }
    /*
    启动一个流程实例
    */
     @Test
    public void test4(){
         ProcessEngine engine=ProcessEngines.getDefaultProcessEngine();
         RuntimeService runtimeService=engine.getRuntimeService();
         String id="runGame";
         ProcessInstance processInstance=runtimeService.startProcessInstanceByKey(id);
         System.out.println("流程定义ID:"+processInstance.getProcessDefinitionId());
         System.out.println("流程实例ID:"+processInstance.getId());
         System.out.println("当前活动ID:"+processInstance.getActivityId());
     }
     /*
     * 任务查询
     * */
    @Test
    public void test5() {
        String assignee = "teacher";
        ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = engine.getTaskService();
        //根据流程key和负责人查询任务
        List<Task> list = taskService.createTaskQuery()
                .processDefinitionKey("runGame")
                .taskAssignee(assignee)
                .list();
        //输出当前用户具有的任务
        for (Task task : list) {
            System.out.println("流程实例ID:" + task.getProcessInstanceId());
            System.out.println("任务ID：" + task.getId());
            System.out.println("负责人："+task.getAssignee());
            System.out.println("任务名：" + task.getName());
        }
    }
    /*
    * 流程任务处理*/
    @Test
    public void test6(){
        ProcessEngine engine=ProcessEngines.getDefaultProcessEngine();
        TaskService taskService= engine.getTaskService();
        Task task=taskService.createTaskQuery()
                .processDefinitionKey("runGame")
                .taskAssignee("teacher")
                .singleResult();
        //完成任务
        taskService.complete(task.getId());
    }
    /*
    * 查询流程定义*/
    @Test
    public void test7(){
        ProcessEngine engine=ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService= engine.getRepositoryService();
        ProcessDefinitionQuery processDefinitionQuery= repositoryService.createProcessDefinitionQuery();
        List<ProcessDefinition> list=processDefinitionQuery.processDefinitionKey("runGame")
                .orderByProcessDefinitionAppVersion()
                .desc()
                .list();
        for(ProcessDefinition processDefinition:list){
            System.out.println("流程定义ID："+processDefinition.getId());
            System.out.println("流程定义名称："+processDefinition.getName());
            System.out.println("流程定义key："+processDefinition.getKey());
            System.out.println("流程定义版本："+processDefinition.getVersion());
            System.out.println("流程部署id："+processDefinition.getDeploymentId());
        }
    }
    /*
    * 删除流程*/
    @Test
    public void test8(){
        ProcessEngine engine=ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService= engine.getRepositoryService();
        repositoryService.deleteDeployment("30001");//（test7）有实例启动的流程会报错
        //删除流程定义，如果该流程已有实例启动时级联删除流程定义
//        repositoryService.deleteDeployment("7501",true);
    }
    /*
    * 流程资源下载（png）*/
    @Test
    public void test9() throws IOException {
        ProcessEngine engine=ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService= engine.getRepositoryService();
        ProcessDefinition processDefinition=repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey("runGame")
//                .orderByProcessDefinitionAppVersion().desc()
                .latestVersion()
                .singleResult();
        String deploymentId=processDefinition.getDeploymentId();
//        InputStream pngInput = repositoryService.getResourceAsStream(deploymentId, processDefinition.getDiagramResourceName())；
        InputStream bpmnInput = repositoryService.getResourceAsStream(deploymentId, processDefinition.getResourceName());
        //文件保存
        File fileBpmn=new File("F:/file/runGame.bpmn");
        OutputStream fileOut=new FileOutputStream(fileBpmn);

        IOUtils.copy(bpmnInput,fileOut);

        bpmnInput.close();
        fileOut.close();
    }
    /*
    * 查看流程历史信息*/
    @Test
    public void Test10(){
        ProcessEngine engine=ProcessEngines.getDefaultProcessEngine();
        HistoryService historyService=engine.getHistoryService();
        HistoricActivityInstanceQuery instanceQuery=historyService.createHistoricActivityInstanceQuery();
        instanceQuery.processDefinitionId("runGame:5:35003");
        instanceQuery.orderByHistoricActivityInstanceStartTime().desc();
        List<HistoricActivityInstance> list=instanceQuery.list();
        for(HistoricActivityInstance historicActivityInstance:list){
            System.out.println(historicActivityInstance.getActivityId());
            System.out.println(historicActivityInstance.getActivityName());
            System.out.println(historicActivityInstance.getActivityType());
            System.out.println(historicActivityInstance.getAssignee());
            System.out.println("-------------------------");
        }
    }
}
