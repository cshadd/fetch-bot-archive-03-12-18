package io.github.cshadd.fetch_bot;
import io.github.cshadd.cshadd_java_data_structures.util.*;

// Main
public class Pathfinding
implements FetchBot {
    // Private Final Instance/Property Fields
    private final Stack<Coordinate> backTrackStack;
    private final int maxX;
    private final int maxY;
    private final PathGraph paths;

    // Private Instance/Property Fields
    private Coordinate currentCoordinate;
    private int direction;

    // Public Constructors
    public Pathfinding() {
        this(0, 0);
    }
    public Pathfinding(int maxX, int maxY) {
        backTrackStack = new Stack<Coordinate>();
        this.maxX = maxX;
        this.maxY = maxY;
        paths = new PathGraph();
        currentCoordinate = new Coordinate(0, 0);
        direction = 90;
    }

    // Protected Static Final Nested Classes
    protected static final class Coordinate
    implements Comparable<Coordinate> {
        // Private Instance/Property Fields
        private int x;
        private int y;

        // Public Constructors
        public Coordinate() {
            this(0, 0);
        }
        public Coordinate(int x, int y) {
            this.x = x;
            this.y = y;
        }

        // Public Property Accessor Methods
        public int getX() {
            return x;
        }
        public int getY() {
            return y;
        }

        // Public Property Mutator Methods
        public void setX(int x) {
            this.x = x;
        }
        public void setY(int y) {
            this.y = y;
        }

        // Public Methods
        public Coordinate coordinateDown() {
            return new Coordinate(x, y - 1);
        }
        public Coordinate coordinateDownLeft() {
            return new Coordinate(x - 1, y - 1);
        }
        public Coordinate coordinateDownRight() {
            return new Coordinate(x + 1, y - 1);
        }
        public Coordinate coordinateLeft() {
            return new Coordinate(x - 1, y);
        }
        public Coordinate coordinateRight() {
            return new Coordinate(x + 1, y);
        }
        public Coordinate coordinateUp() {
            return new Coordinate(x, y + 1);
        }
        public Coordinate coordinateUpLeft() {
            return new Coordinate(x - 1, y + 1);
        }
        public Coordinate coordinateUpRight() {
            return new Coordinate(x + 1, y + 1);
        }
        public float length() {
            return (float)Math.sqrt(x*x + y*y);
        }

        // Public Methods (Overrided)
        @Override
        public int compareTo(Coordinate other) {
            float l = length();
            float l2 = other.length();
            if (l < l2) {
                return -1;
            }
            else if (l > l2) {
                return 1;
            }
            else {
                return 0;
            }
        }
        @Override
        public String toString() {
            return "(" + x + ", " + y + ")";
        }
    }
    protected final class PathGraph
    extends UndirectedGraph<Coordinate> {
        // Public Constructors
        public PathGraph() {
            super();
            setRoot(vertex(currentCoordinate));
        }

        // Public Methods
        public Coordinate next() {
            /*final Coordinate down = currentCoordinate.coordinateDown();
            final Coordinate left = currentCoordinate.coordinateLeft();
            final Coordinate right = currentCoordinate.coordinateRight();
            final Coordinate up = currentCoordinate.coordinateUp();*/

            // if down is not blocked...
            // paths.addEdge(currentCoordinate, down);
            // else if 180 direction become angry...

            // if left is not blocked...
            // paths.addEdge(currentCoordinate, left);
            // else if 270 direction become angry cause facing direction...

            // if right is not blocked...
            // paths.addEdge(currentCoordinate, right);
            // else if 90 direction become angry cause facing direction...

            // if up is not blocked...
            // paths.addEdge(currentCoordinate, up);
            // else if 0 direction become angry cause facing direction...

            // if all are blocked become sad and just remain stuck...
            // else if one or more avalible...
            // choose (random) one that is not visited and go there, pushing the opposite into stack...
            // if all are visited then pop one from stack to move...
            // if popped all the way to end and cannot find object become sad and remain stuck...
            // rotate 360 just to get a view... (Maybe!)
            // if find object at avalible node become happy

            return null;
        }
    }
}
