import org.junit.Test;

import java.security.SecureRandom;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

public class testRubixImmutable {
    public static enum Color {
        W, R, B, G, O, Y
    }

    public static enum Orientation {
        left, right, up, down, forward, backward
    }

    public static enum MoveDirection {
        left, right, up, down, twistLeft, twistRight
    }

    public static class Side {
        private final Color c;
        private Orientation o;

        public Side(Color c, Orientation o) {
            this.c = c;
            this.o = o;
        }

        public Side a(Color c, Orientation o) {
            return new Side(c, o);
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

        public Side move(MoveDirection move) {
            if (move == MoveDirection.left) {
                if (o == Orientation.down) return this;
                else if (o == Orientation.up) return this;
                else if (o == Orientation.left) return a(c, Orientation.backward);
                else if (o == Orientation.right) return a(c, Orientation.forward);
                else if (o == Orientation.forward) return a(c, Orientation.left);
                else if (o == Orientation.backward) return a(c, Orientation.right);
            } else if (move == MoveDirection.right) {
                if (o == Orientation.down) return this;
                else if (o == Orientation.up) return this;
                else if (o == Orientation.left) return a(c, Orientation.forward);
                else if (o == Orientation.right) return a(c, Orientation.backward);
                else if (o == Orientation.forward) return a(c, Orientation.right);
                else if (o == Orientation.backward) return a(c, Orientation.left);
            } else if (move == MoveDirection.up) {
                if (o == Orientation.down) return a(c, Orientation.forward);
                else if (o == Orientation.up) return a(c, Orientation.backward);
                else if (o == Orientation.left) return this;
                else if (o == Orientation.right) return this;
                else if (o == Orientation.forward) return a(c, Orientation.up);
                else if (o == Orientation.backward) return a(c, Orientation.down);
            } else if (move == MoveDirection.down) {
                if (o == Orientation.down) return a(c, Orientation.backward);
                else if (o == Orientation.up) return a(c, Orientation.forward);
                else if (o == Orientation.left) return this;
                else if (o == Orientation.right) return this;
                else if (o == Orientation.forward) return a(c, Orientation.down);
                else if (o == Orientation.backward) return a(c, Orientation.up);
            } else if (move == MoveDirection.twistLeft) {
                if (o == Orientation.down) return a(c, Orientation.right);
                else if (o == Orientation.up) return a(c, Orientation.left);
                else if (o == Orientation.left) return a(c, Orientation.down);
                else if (o == Orientation.right) return a(c, Orientation.up);
                else if (o == Orientation.forward) return this;
                else if (o == Orientation.backward) return this;
            } else if (move == MoveDirection.twistRight) {
                if (o == Orientation.down) return a(c, Orientation.left);
                else if (o == Orientation.up) return a(c, Orientation.right);
                else if (o == Orientation.left) return a(c, Orientation.up);
                else if (o == Orientation.right) return a(c, Orientation.down);
                else if (o == Orientation.forward) return this;
                else if (o == Orientation.backward) return this;
            }
            return this;
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

        public Piece move(MoveDirection move) {
            return new Piece(sides.stream().map(s -> s.move(move)).collect(Collectors.toList()));
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

        public Cube move(Move move) {
            return move(move.getFace(), move.getDirection());
        }

        public Cube move(Orientation face, MoveDirection direction) {
//            System.out.println("**" + face.toString() + " " + direction.toString() + "**");
            return new Cube(pieces.stream()
                    .map(p -> {
                        if (p.onSide(face)) {
                            return p.move(direction);
                        } else {
                            return p;
                        }
                    })
                    .collect(Collectors.toList()));
        }

        public String color(Orientation face, Orientation o1, Orientation o2) {
            return pieces.stream().filter(p -> p.onSide(face) && p.onSide(o1) && p.onSide(o2)).map(p -> p.color(face)).findFirst().map(c -> c.toString()).orElse("?");
        }

        public String toString() {
            return "\n" + color(Orientation.up, Orientation.left, Orientation.backward) + color(Orientation.up, Orientation.right, Orientation.backward) + "   \n" +
                    color(Orientation.up, Orientation.left, Orientation.forward) + color(Orientation.up, Orientation.right, Orientation.forward) + "   \n" +
                    color(Orientation.forward, Orientation.left, Orientation.up) + color(Orientation.forward, Orientation.right, Orientation.up) + color(Orientation.right, Orientation.forward, Orientation.up) + color(Orientation.right, Orientation.backward, Orientation.up) + color(Orientation.left, Orientation.backward, Orientation.up) + color(Orientation.left, Orientation.forward, Orientation.up) + "   \n" +
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

    public static <T extends Enum<?>> T randomEnum(Class<T> clazz) {
        int x = random.nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }

    public enum Move {
        left_up(Orientation.left, MoveDirection.up),
        left_down(Orientation.left, MoveDirection.down),
        right_up(Orientation.right, MoveDirection.up),
        right_down(Orientation.right, MoveDirection.down),
        up_left(Orientation.up, MoveDirection.left),
        up_right(Orientation.up, MoveDirection.right),
        down_left(Orientation.down, MoveDirection.left),
        down_right(Orientation.down, MoveDirection.right),
        forward_twistLeft(Orientation.forward, MoveDirection.twistLeft),
        forward_twistRight(Orientation.forward, MoveDirection.twistRight),
        backward_twistLeft(Orientation.backward, MoveDirection.twistLeft),
        backward_twistRight(Orientation.backward, MoveDirection.twistRight);
        private final Orientation face;
        private final MoveDirection direction;

        Move(Orientation face, MoveDirection direction) {
            this.face = face;
            this.direction = direction;
        }


        public Orientation getFace() {
            return face;
        }

        public MoveDirection getDirection() {
            return direction;
        }
    }

    @Test
    public void print() {
        System.out.println("#rubix");
        Cube cube = Cube.a();
        System.out.print(cube.toString());
        cube.move(Orientation.left, MoveDirection.up);
        System.out.print(cube.toString());
        cube.move(Orientation.left, MoveDirection.down);
        System.out.print(cube.toString());
        cube.move(Orientation.right, MoveDirection.up);
        System.out.print(cube.toString());
        cube.move(Orientation.right, MoveDirection.down);
        System.out.print(cube.toString());
        cube.move(Orientation.up, MoveDirection.left);
        System.out.print(cube.toString());
        cube.move(Orientation.up, MoveDirection.right);
        System.out.print(cube.toString());
        cube.move(Orientation.down, MoveDirection.right);
        System.out.print(cube.toString());
        cube.move(Orientation.down, MoveDirection.left);
        System.out.print(cube.toString());


        cube.move(Orientation.left, MoveDirection.up);
        cube.move(Orientation.right, MoveDirection.up);
        cube.move(Orientation.left, MoveDirection.up);
        cube.move(Orientation.right, MoveDirection.up);
        System.out.print(cube.toString());
    }


    @Test
    public void left() {
        Piece piece = new Piece(Arrays.asList(new Side(Color.Y, Orientation.up), new Side(Color.O, Orientation.left), new Side(Color.B, Orientation.forward)));
        System.out.println(piece);
        piece.move(MoveDirection.left);
        System.out.println(piece);
    }

    public class State {
        public State(Cube cube, List<Move> moves) {
            this.cube = cube;
            this.moves = moves;
        }

        public final Cube cube;
        public final List<Move> moves;

        public Cube getCube() {
            return cube;
        }

        public List<Move> getMoves() {
            return moves;
        }

        @Override
        public String toString() {
            return "State{" +
                    "moves=" + moves +
                    ", cube=" + cube +
                    '}';
        }
    }

    @Test
    public void equals() {
        Cube cube1 = Cube.a();
        Cube cube2 = Cube.a();

        assertThat(cube1, equalTo(cube2));
        cube1 = movePrint(cube1, Move.forward_twistLeft);
        assertThat(cube1, not(equalTo(cube2)));
        cube1 = movePrint(cube1, Move.forward_twistRight);
        assertThat(cube1, equalTo(cube2));
        cube1 = movePrint(cube1, Move.backward_twistLeft);
        assertThat(cube1, not(equalTo(cube2)));
        cube1 = movePrint(cube1, Move.backward_twistLeft);
        assertThat(cube1, equalTo(cube2));
    }

    private Cube movePrint(Cube cube1, Move forward_twistLeft) {
        cube1 = cube1.move(forward_twistLeft);
        System.out.print(cube1.toString());
        return cube1;
    }

    public <T> List<T> concat(List<T> a, List<T> b) {
        List<T> all = new ArrayList<T>(a);
        all.addAll(b);
        return all;
    }

    public <T> List<T> concat(List<T> a, T b) {
        List<T> all = new ArrayList<T>(a);
        all.add(b);
        return all;
    }

    public State getStates(List<State> active_states,
                           List<State> states,
                           List<Cube> cubes,
                           Predicate<Cube> solved) {
        System.out.println(" active_states:" + Integer.toString(active_states.size()) + " active_states:" + Integer.toString(states.size()) + " cubes:" + Integer.toString(cubes.size()));
        if (active_states.size() == 0) throw new RuntimeException("impossible");
        else {
            List<State> new_states = active_states.stream()
                    .flatMap(a -> Arrays.stream(Move.values()).map(m -> {
                        Cube c = a.getCube().move(m);
                        List<Move> moves = concat(a.getMoves(), m);
                        return new State(c, moves);
                    }))
                    .filter(s -> !cubes.contains(s.getCube()))
                    .collect(Collectors.toList());

            Optional<State> solution = new_states.stream().filter(s -> solved.test(s.getCube())).findFirst();
            if (solution.isPresent()) {
                return solution.get();
            } else {
                List<Cube> new_cubes = new_states.stream().map(s -> s.getCube()).collect(Collectors.toList());
                List<Cube> updated_cubes = concat(cubes, new_cubes);
                List<State> updated_states = concat(states, new_states);
                return getStates(new_states, updated_states, updated_cubes, solved);
            }
        }
    }

    @Test
    public void solution() {
        Cube cube = Cube.a();
        State state = new State(cube, Collections.EMPTY_LIST);
        State solution = getStates(Collections.singletonList(state), Collections.singletonList(state), Collections.singletonList(cube), c->cube.move(Move.backward_twistLeft).equals(c));
        System.out.println(solution);
    }
}
