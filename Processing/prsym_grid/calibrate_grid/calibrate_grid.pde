float xw = 162;
float yh = 120;

float xx = 0;
float yy = 0;

void setup() {
  size(3840, 1080);
}


void draw() {
  
  
  
  translate(xx,yy);
  
  background(255);

  fill(0);
  textSize(32);
  text(xw + " X " + yh, 10, yh/2);
  text(xx + "", 10, yh*2);

  
  stroke(0);
  for (float x = 0; x < width; x+= xw) {
    line(x, 0, x, height);
  }
  for (float y = 0; y < height; y += yh) {
    line(0, y, width, y);
  }
}

void keyPressed() {
  if (key == 'a') {
    yh-=0.1;
  }  
  else if (key == 'z') {
    yh+=0.1;
  }

  if (key == 'q') {
    xw-=0.1;
  }  
  else if (key == 'w') {
    xw+=0.1;
  }
  
  
  if (key == 'f') {
    xx += 0.5;
  } else if (key == 'd') {
    xx += -0.1; 
  }
}


