plot_results <- function(file, type, xlab = "Number of vertices", ylim = NULL) {

data <- read.csv(paste("results/", file, "-", type, ".csv", sep=""), header = T , sep = ",") 

#pdf(file = paste(type, ".pdf", sep = ""))

if (type == "time") {
  type_label = "Time (ms)"
} else {
  type_label = "Memory (MB)"
}

if (length(ylim)==0) {
  ylim = range(data[2:ncol(data)])
}

#options(scipen=999)
#bltr
#par(oma=c(0,0,0,0))
par(mar = c(4, 4, 0.5, 0.5))  

plot(data$Args, data$Graph4J, type = "o", pch = 0, lty = 1,
  xlab = xlab, ylab = type_label, cex.lab = 0.8, 
  xlim = range(data$Args), 
  ylim = ylim,
  axes = T, cex.axis = 0.8,
  ann = T, lwd = 2)
  

pch = c(0)
if( length(data$JGraphT) > 0) {
  lines(data$Args, data$JGraphT, type = "o", pch = 1, lty = 1)
  pch <- append(pch, 1, after = length(pch))
}
if( length(data$Guava) > 0) {
  lines(data$Args, data$Guava, type = "o", pch = 2, lty = 1)
  pch <- append(pch, 2, after = length(pch))
}
if( length(data$JUNG) > 0) {
  lines(data$Args, data$JUNG, type = "o", pch = 3, lty = 1)
  pch <- append(pch, 3, after = length(pch))
}

legend("topleft", colnames(data)[2:ncol(data)], cex = 0.8, pch = pch, lty = 1);
#title(main="Title here")

#dev.off()
}