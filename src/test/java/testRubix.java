import org.junit.Test;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

public class testRubix {
    public static enum Color {
        W, R, B, G, O, Y
    }

    public static enum Orientation {
        left, right, up, down, forward, backward
    }

    public static class Side {
        private final Color c;
        private Orientation o;

        public Side(Color c, Orientation o) {
            this.c = c;
            this.o = o;
        }

        public Color getC() {
            return c;
        }

        public Orientation getO() {
            return o;
        }

        public void setO(Orientation o) {
            this.o = o;
        }

        public void move(Orientation move) {
            if (move == Orientation.left) {
                if (o == Orientation.down) return;
                else if (o == Orientation.up) return;
                else if (o == Orientation.left) o = Orientation.backward;
                else if (o == Orientation.right) o = Orientation.forward;
                else if (o == Orientation.forward) o = Orientation.left;
                else if (o == Orientation.backward) o = Orientation.right;
            } else if (move == Orientation.right) {
                if (o == Orientation.down) return;
                else if (o == Orientation.up) return;
                else if (o == Orientation.left) o = Orientation.forward;
                else if (o == Orientation.right) o = Orientation.backward;
                else if (o == Orientation.forward) o = Orientation.right;
                else if (o == Orientation.backward) o = Orientation.left;
            } else if (move == Orientation.up) {
                if (o == Orientation.down) o = Orientation.forward;
                else if (o == Orientation.up) o = Orientation.backward;
                else if (o == Orientation.left) return;
                else if (o == Orientation.right) return;
                else if (o == Orientation.forward) o = Orientation.up;
                else if (o == Orientation.backward) o = Orientation.down;
            } else if (move == Orientation.down) {
                if (o == Orientation.down) o = Orientation.backward;
                else if (o == Orientation.up) o = Orientation.forward;
                else if (o == Orientation.left) return;
                else if (o == Orientation.right) return;
                else if (o == Orientation.forward) o = Orientation.down;
                else if (o == Orientation.backward) o = Orientation.up;
            }
        }


        @Override
        public String toString() {
            return "{" + c + ", " + o +
                    '}';
        }

        @Override
        public boolean equals(Object o1) {
            if (this == o1) return true;
            if (o1 == null || getClass() != o1.getClass()) return false;
            Side side = (Side) o1;
            return c == side.c &&
                    o == side.o;
        }

        @Override
        public int hashCode() {
            return Objects.hash(c, o);
        }
    }

    public static class colorPrint {
        int x;
        int y;
        Color c;
    }

    public static class Piece {
        private final List<Side> sides;

        public Piece(List<Side> sides) {
            this.sides = sides;
        }

        public List<Side> getSides() {
            return sides;
        }

        public void move(Orientation move) {
            sides.stream().forEach(s->s.move(move));
        }

        public boolean onSide(Orientation o) {
            return sides.stream().anyMatch(s -> s.getO() == o);
        }

        public Color color(Orientation face) {
            return sides.stream().filter(s -> s.o == face).findFirst().get().getC();
        }

        @Override
        public String toString() {
            return sides.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Piece piece = (Piece) o;
            return Objects.equals(sides, piece.sides);
        }

        @Override
        public int hashCode() {
            return Objects.hash(sides);
        }
    }

    public static class Cube {
        private final List<Piece> pieces;

        public Cube(List<Piece> pieces) {
            this.pieces = pieces;
        }

        public static Cube a() {
            List<Piece> pieces = Arrays.asList(
                    new Piece(Arrays.asList(new Side(Color.Y, Orientation.up), new Side(Color.O, Orientation.left), new Side(Color.B, Orientation.forward))),
                    new Piece(Arrays.asList(new Side(Color.Y, Orientation.up), new Side(Color.O, Orientation.left), new Side(Color.G, Orientation.backward))),
                    new Piece(Arrays.asList(new Side(Color.Y, Orientation.up), new Side(Color.R, Orientation.right), new Side(Color.B, Orientation.forward))),
                    new Piece(Arrays.asList(new Side(Color.Y, Orientation.up), new Side(Color.R, Orientation.right), new Side(Color.G, Orientation.backward))),
                    new Piece(Arrays.asList(new Side(Color.W, Orientation.down), new Side(Color.O, Orientation.left), new Side(Color.B, Orientation.forward))),
                    new Piece(Arrays.asList(new Side(Color.W, Orientation.down), new Side(Color.O, Orientation.left), new Side(Color.G, Orientation.backward))),
                    new Piece(Arrays.asList(new Side(Color.W, Orientation.down), new Side(Color.R, Orientation.right), new Side(Color.B, Orientation.forward))),
                    new Piece(Arrays.asList(new Side(Color.W, Orientation.down), new Side(Color.R, Orientation.right), new Side(Color.G, Orientation.backward)))
            );
            return new Cube(pieces);
        }

        public void move(Move move) {
            move(move.getFace(), move.getDirection());
        }

        public void move(Orientation face, Orientation direction) {
            System.out.println("**" + face.toString() + " " + direction.toString() + "**");
            pieces.stream().filter(p -> p.onSide(face)).forEach(p->p.move(direction));
        }

        public String color(Orientation face, Orientation o1, Orientation o2) {
            return pieces.stream().filter(p -> p.onSide(face) && p.onSide(o1) && p.onSide(o2)).map(p -> p.color(face)).findFirst().map(c -> c.toString()).orElse("?");
        }

        public String toString() {
            return "\n" + color(Orientation.up, Orientation.left, Orientation.backward) + color(Orientation.up, Orientation.right, Orientation.backward) + "   \n" +
                    color(Orientation.up, Orientation.left, Orientation.forward) + color(Orientation.up, Orientation.right, Orientation.forward) + "   \n" +
                    color(Orientation.forward, Orientation.left, Orientation.up) + color(Orientation.forward, Orientation.right, Orientation.up) + color(Orientation.right, Orientation.forward, Orientation.up) + color(Orientation.right, Orientation.backward, Orientation.up) + color(Orientation.left, Orientation.backward, Orientation.up) + color(Orientation.left, Orientation.forward, Orientation.up) +  "   \n" +
                    color(Orientation.forward, Orientation.left, Orientation.down) + color(Orientation.forward, Orientation.right, Orientation.down) + color(Orientation.right, Orientation.forward, Orientation.down) + color(Orientation.right, Orientation.backward, Orientation.down) + color(Orientation.left, Orientation.backward, Orientation.down) + color(Orientation.left, Orientation.forward, Orientation.down) + "   \n" +
                    color(Orientation.down, Orientation.left, Orientation.forward) + color(Orientation.down, Orientation.right, Orientation.forward) + "   \n" +
                    color(Orientation.down, Orientation.left, Orientation.backward) + color(Orientation.down, Orientation.right, Orientation.backward) + "   \n" +
                    color(Orientation.backward, Orientation.left, Orientation.down) + color(Orientation.backward, Orientation.right, Orientation.down) + "   \n" +
                    color(Orientation.backward, Orientation.left, Orientation.up) + color(Orientation.backward, Orientation.right, Orientation.up) + "   \n\n"
                    ;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Cube cube = (Cube) o;
            return Objects.equals(pieces, cube.pieces);
        }

        @Override
        public int hashCode() {
            return Objects.hash(pieces);
        }
    }
    private static final SecureRandom random = new SecureRandom();

    public static <T extends Enum<?>> T randomEnum(Class<T> clazz){
        int x = random.nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }
    public enum Move {
        left_up(Orientation.left, Orientation.up),
        left_down(Orientation.left, Orientation.down),
        right_up(Orientation.right, Orientation.up),
        right_down(Orientation.right, Orientation.down),
        up_left(Orientation.up, Orientation.left),
        up_right(Orientation.up, Orientation.right),
        down_left(Orientation.down, Orientation.left),
        down_right(Orientation.down, Orientation.right);
        private final Orientation face;
        private final Orientation direction;

        Move(Orientation face, Orientation direction) {
            this.face = face;
            this.direction = direction;
        }


        public Orientation getFace() {
            return face;
        }

        public Orientation getDirection() {
            return direction;
        }
    }
    @Test
    public void print() {
        System.out.println("#rubix");
        Cube cube = Cube.a();
        System.out.print(cube.toString());
        cube.move(Orientation.left, Orientation.up);
        System.out.print(cube.toString());
        cube.move(Orientation.left, Orientation.down);
        System.out.print(cube.toString());
        cube.move(Orientation.right, Orientation.up);
        System.out.print(cube.toString());
        cube.move(Orientation.right, Orientation.down);
        System.out.print(cube.toString());
        cube.move(Orientation.up, Orientation.left);
        System.out.print(cube.toString());
        cube.move(Orientation.up, Orientation.right);
        System.out.print(cube.toString());
        cube.move(Orientation.down, Orientation.right);
        System.out.print(cube.toString());
        cube.move(Orientation.down, Orientation.left);
        System.out.print(cube.toString());


        cube.move(Orientation.left, Orientation.up);
        cube.move(Orientation.right, Orientation.up);
        cube.move(Orientation.left, Orientation.up);
        cube.move(Orientation.right, Orientation.up);
        System.out.print(cube.toString());
    }


    @Test
    public void left() {
        Piece piece = new Piece(Arrays.asList(new Side(Color.Y, Orientation.up), new Side(Color.O, Orientation.left), new Side(Color.B, Orientation.forward)));
        System.out.println(piece);
        piece.move(Orientation.left);
        System.out.println(piece);
    }

    @Test
    public void equals() {
        Cube cube1 = Cube.a();
        Cube cube2 = Cube.a();

        assertThat(cube1, equalTo(cube2));
        cube1.move(Move.down_left);
        assertThat(cube1, not(equalTo(cube2)));
        cube2.move(Move.down_left);
        assertThat(cube1, equalTo(cube2));
    }
}
