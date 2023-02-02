create_eps <- function(file, xlab = "Number of vertices", ylim1 = NULL, ylim2 = NULL) {

setEPS()
postscript(paste("paper/eps/",file,".eps",sep=""), width=7, height=4)
par(mfrow=c(1,2))
plot_results(file, "time", xlab, ylim1)
plot_results(file, "memory", xlab, ylim2)
dev.off()

}