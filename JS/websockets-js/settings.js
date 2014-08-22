// Where are you? What are you doing?
var MODES = { LOCAL : 0, REMOTE : 1 };
// Pick a mode
var MODE = MODES.LOCAL;

// Change IP address depending on location
var IP = MODE == MODES.LOCAL ? 'http://127.0.0.1:8080/' : 'http://server.address.goes.here/';

// Total number of screens
var NUM_SCREENS = 3;
// Width of each screen
var WIDTH = 3840;
// Height of each screen
var HEIGHT = 1080;

// Total width across all screens
var TOTAL_WIDTH = NUM_SCREENS*WIDTH;

// Scale things down in LOCAL mode
var SCALE = MODE == MODES.LOCAL ? .125 : 1;

// Calibration - Changes with Scale
var XMARGIN = WIDTH*SCALE*0.1;
var YMARGIN = HEIGHT*SCALE*0.1;

// Framerate as measured in milliseconds (NOT frames were second)
var FRAMERATE_IN_MS = 30;
