setwd("d:/java/Graph")
paperDir <- "d:/articole/!graph4j/"

source("R/plot_results.R")
source("R/create_eps.R")


create_eps("EmptyGraphDemo", xlab = "Millions of vertices")
create_eps("CompleteGraphDemo1", ylim1=c(0,7000)) #all
create_eps("CompleteGraphDemo2") #Guava only
create_eps("SparseGraphDemo", "Vertex degrees", ylim1=c(0,10000))
create_eps("CopyGraphDemo", ylim1=c(0,4000))


#create_eps("RemoveEdgesDemo")
#create_eps("RemoveNodesDemo")

setEPS()
postscript(paste(paperDir, "eps/RemoveEdgesNodes.eps",sep=""), width=7, height=3.5)
par(mfrow=c(1,2))
plot_results("RemoveEdgesDemo", "time", ylim=c(0,2000))
plot_results("RemoveNodesDemo", "time")
dev.off()


setEPS()
postscript(paste(paperDir, "eps/IterateDemo.eps",sep=""), width=7, height=3.5)
par(mfrow=c(1,2))
plot_results("IterateSuccessorsDemo", "time")
plot_results("IteratePredecessorsDemo", "time")
dev.off()

	
create_eps("DFSIteratorDemo", ylim1=c(0,9000))
create_eps("BFSIteratorDemo")

#create_eps("LabeledGraphDemo", ylim1=c(0,2000)))

setEPS()
postscript(paste(paperDir, "eps/LabeledWeightedDemo.eps",sep=""), width=7, height=3.5)
par(mfrow=c(1,2))
plot_results("LabeledGraphDemo", "memory")
plot_results("WeightedGraphDemo", "memory")
dev.off()


create_eps("DijkstraDemo1")
create_eps("DijkstraDemo2") #Jung only

setEPS()
postscript(paste(paperDir, "eps/DijkstraDemo.eps",sep=""), width=7, height=3.5)
par(mfrow=c(1,2))
plot_results("DijkstraDemo1", "time")
plot_results("DijkstraDemo2", "time")
dev.off()


#create_eps("PrimMSTDemo")
#create_eps("KruskalMSTDemo")

setEPS()
postscript(paste(paperDir, "eps/MSTDemo.eps",sep=""), width=7, height=3.5)
par(mfrow=c(1,2))
plot_results("PrimMSTDemo", "time")
plot_results("KruskalMSTDemo", "time")
dev.off()

create_eps("EdmondsKarpDemo1")

create_eps("HopcroftKarpDemo")

#setEPS()
#postscript(paste(paperDir, "eps/XYDemo.eps",sep=""), width=7, height=3.5)
#par(mfrow=c(1,2))
#plot_results("XDemo", "time")
#plot_results("YDemo", "time")
#dev.off()
