void setup() {
  size(3840, 1080);
}

int y = 1000;
int x = 500;

void draw() {
  background(0);

  fill(255);
  textSize(32);
  text(""+y, width/2, y-10);
  stroke(255);
  line(0, y, width, y);

  text(""+x, x+10, height/2);
  stroke(255);
  line(x, 0, x, height);
}

void keyPressed() {
  if (key == 'a') {
    y++;
  }  
  else if (key == 'z') {
    y--;
  }

  if (key == 's') {
    y+=10;
  }  
  else if (key == 'x') {
    y-=10;
  }
  
    if (key == 'w') {
    x+=10;
  }  
  else if (key == 'q') {
    x-=10;
  }

    if (key == '2') {
    x++;
  }  
  else if (key == '1') {
    x--;
  }

  
}

