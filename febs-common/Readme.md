                        处理资源服务器异常
资源服务器异常主要有两种：令牌不正确返回401和用户无权限返回403。因为资源服务器有多个，所以相关的异常处理类可以定义在febs-common通用模块里。

1.在febs-common模块cc.mrbird.febs.common路径下新建handler包，然后在该包下新建FebsAuthExceptionEntryPoint





















