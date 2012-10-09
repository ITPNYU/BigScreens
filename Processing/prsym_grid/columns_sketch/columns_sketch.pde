size(11520,1080);
background(0);
strokeWeight(8);
stroke(255);

textSize(48);
fill(255);

int x1a = 3376;
int x1b = 3736;
int x2a = 3840+2561;
int x2b = 3840+2975;
int x3a = 7680+2108;
int x3b = 7680+2398;

line(x1a,0,x1a,height);
line(x1b,0,x1b,height);
line(x2a,0,x2a,height);
line(x2b,0,x2b,height);
line(x3a,0,x3a,height);
line(x3b,0,x3b,height);

text(x1a,x1a+20,100);
text(x1b,x1b+20,100);
text(x2a,x2a+20,100);
text(x2b,x2b+20,100);
text(x3a,x3a+20,100);
text(x3b,x3b+20,100);

int ybottom = 973;
line(0,ybottom,width,ybottom);
text(ybottom,100,ybottom-20);

saveFrame("columns.png");


