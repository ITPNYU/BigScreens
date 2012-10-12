import processing.video.*;


Movie[] movies;

//--------------------------------------
void setup() {
  size(800, 600);
  movies = new Movie[10];
  for (int i = 0; i < movies.length; i++) {
    movies[i] = new Movie(this, "station.mov");
    movies[i].loop();
  }  
}

void movieEvent(Movie m) {
  m.read();
}

void draw() {
  background(255);
  randomSeed(1);
  for (int i = 0; i < movies.length; i++) {
    float x = random(width);
    float y = random(height);
    image(movies[i], x, y, 64, 64);
  }
}

