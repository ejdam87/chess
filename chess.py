from typing import List, Tuple, Optional, Callable
import sys

Board = List[List[int]]     # Board is 2D matrix
Figure = Tuple[str, int]    # Figure is tuple of figure's name and number of plazer
Cell = Tuple[int, int]      # Cell is (x, y) coord on board

SIZE = 8    # Chessboard will contain SIZE x SIZE cells


sys.stderr = open("x.txt", "w")


class Chess:

    def __init__(self) -> None:
        
        self.size = SIZE
        self.board = [["empty" for i in range(self.size)] for j in range(self.size)]

        # Representation of figures in text format
        self.representation_of = {
            "pawn": "P",
            "horse" : "H",
            "tower" : "T",
            "shooter" : "S",
            "king" : "K",
            "queen" : "Q"
        }

        # Assign every figure it's moving method
        self.assign_move = {
            "pawn": self.move_pawn,
            "horse" : self.move_horse,
            "tower" : self.move_tower,
            "shooter" : self.move_shooter,
            "king" : self.move_king,
            "queen" : self.move_queen
        }

        # Initialize king's positions (need to be done by setup method)
        self.king_pos = {}

        self.setup_classic_board()

        # self.setup_test()

    def move(self, from_: Cell, to: Cell) -> None:
        """
        Method to move figure from one cell to another
        """
        oldx, oldy = from_
        newx, newy = to

        figure = self.board[oldy][oldx]
        self.board[oldy][oldx] = "empty"
        self.board[newy][newx] = figure

    # --- Helpers ---



    # --- Coordinate transformation (e.g from A5 to col 0 row 5)
    def from_formatted(self, with_letter: str) -> Cell:

        col = ord(with_letter[0]) - ord("A")
        row = int(with_letter[1])

        return col, row

    def from_cell(self, cell: Cell) -> str:
        
        col, row = cell

        return chr(ord("A") + col) + str(row)
    # ---


    def is_inbound(self, x: int, y: int) -> bool:
        # Checks whether player's pick is in-bounds
        return not (x < 0 or x >= self.size or y < 0 or y >= self.size)

    def check_mate(self, player: int, danger: bool) -> bool:

        if not danger:
            return False

        x, y = self.king_pos[player]

        possibilities = self.move_king(x, y)

        # Try to move king every possible cell and check whether we
        # can find any cell where he is not in danger anymore
        # if not successful => it is mate

        for poss in possibilities:
            
            old = self.king_pos[player]
            self.king_pos[player] = poss

            if not self.is_endangered(player):
                self.king_pos[player] = old
                return False

            self.king_pos[player] = old

        return True

    def is_endangered(self, player: int) -> bool:
        """
        Checks whether king's endangered
        """
        x, y = self.king_pos[player]
        opponent = 1 if player == 2 else 2
        possible = []

        # Get every possible cell opponent can get
        # and find out if player's king cell is in it
        for y in range(len(self.board)):
            for x in range(len(self.board[0])):

                # is opponent's figure
                cell = self.board[y][x]
                if cell != "empty" and cell[1] == opponent:
                    
                    figure = cell[0]
                    move_method = self.assign_move[figure]

                    if figure == "pawn":
                        possible += move_method(x, y, opponent)
                    else:
                        possible += move_method(x, y)

        return self.king_pos[player] in possible


    def get_able_to_move(self, player: int) -> List[Figure]:
        
        res = []
        for row in self.board:
            for cell in row:

                if cell == "empty":
                    continue

                if cell[1] == player:
                    res.append(cell)

        return res



    # --- Setting boards
    def setup_test(self) -> None:

        self.board[3][3] = ("king", 2)
        self.board[2][0] = ("tower", 1)
        self.board[1][0] = ("king", 1)

        self.king_pos[1] = (0, 1)
        self.king_pos[2] = (3, 3)

    def setup_classic_board(self) -> None:
        
        self.board[1] = [("pawn", 2) for i in range(self.size)]
        self.board[6] = [("pawn", 1) for i in range(self.size)]

        self.board[0] = [("tower", 2), ("horse", 2), ("shooter", 2), ("king", 2),
                         ("queen", 2), ("shooter", 2), ("horse", 2), ("tower", 2)]

        self.board[7] = [("tower", 1), ("horse", 1), ("shooter", 1), ("king", 1),
                         ("queen", 1), ("shooter", 1), ("horse", 1), ("tower", 1)]

        self.king_pos[1] = (3, 7)
        self.king_pos[2] = (3, 0)
    # ---



    def draw_board(self) -> None:

        numbering = f" X "
        # Column numbering
        print(" " * len(numbering), end = "")
        for col in range(self.size):
            letter = chr(ord("A") + col)
            print(f"  {letter}  ", end = "")
        print()

        line = "+" + "----+" * self.size
        for row in range(self.size):
            
            numbering = f" {row} "
            print(" " * len(numbering) + line)
            print(numbering + "|", end="")

            for col in range(self.size):

                if self.board[row][col] == "empty":
                    to_print  = "    "
                else:
                    figure, player = self.board[row][col]

                    # If current position is non-empty
                    if figure in self.representation_of:
                        to_print = f" {self.representation_of[figure]}{player} "

                print(to_print, end="|")

            print(numbering, end = "")
            print()

        print(" " * len(numbering) + line)

        # Column numbering
        print(" " * len(numbering), end = "")
        for col in range(self.size):
            letter = chr(ord("A") + col)
            print(f"  {letter}  ", end = "")
        print()

    def figure_on(self, x: int, y: int) -> Optional[str]:

        if x < 0 or x >= self.size or y < 0 or y >= self.size:
            return None

        if self.board[y][x] == "empty":
            return None

        return self.board[y][x][0]

    def filter_options(self, x: int, y: int,
                       options: List[Cell]) -> List[Cell]:
        
        filtered = []
        for dx, dy in options:
            
            if dx < 0 or dx >= self.size:
                continue

            if dy < 0 or dy >= self.size:
                continue

            if self.board[dy][dx] == "empty":
                filtered.append((dx, dy))
                continue

            if self.board[y][x][1] == self.board[dy][dx][1]:
                continue

            filtered.append((dx, dy))

        return filtered

    # ---
    # --- Moving figures

    # Player 1 always down !!!
    def move_pawn(self, x: int, y: int, player: int) -> List[Cell]:

        options = []

        half = self.size // 2

        if player == 1:
            # Going up
            if self.figure_on(x + 1, y - 1):
                options.append((x + 1, y - 1))

            if self.figure_on(x - 1, y - 1):
                options.append((x - 1, y - 1))

            if not self.figure_on(x, y - 1):
                options.append((x, y - 1))

            if y - 2 >= half:
                if not self.figure_on(x, y - 2):
                    options.append((x, y - 2))
        else:
            # Going down
            if self.figure_on(x + 1, y + 1):
                options.append((x + 1, y + 1))

            if self.figure_on(x - 1, y + 1):
                options.append((x - 1, y + 1))

            if not self.figure_on(x, y + 1):
                options.append((x, y + 1))

            if y + 2 <= half:
                if not self.figure_on(x, y + 2):
                    options.append((x, y + 2))

        return self.filter_options(x, y, options)

    def move_king(self, x: int, y: int) -> List[Cell]:
        
        options = []

        for dx in range(-1, 2):
            for dy in range(-1, 2):

                if dx == dy == 0:
                    continue

                options.append((x + dx, y + dy))

        return self.filter_options(x, y, options)

    def move_queen(self, x: int, y: int) -> List[Cell]:
        
        options = self.move_tower(x, y)
        options += self.move_shooter(x, y)
        return list(set(options))

    def move_tower(self, x: int, y: int) -> List[Cell]:
        
        options = []

        dx = x - 1
        while dx >= 0:
            options.append((dx, y))
            if self.figure_on(dx, y):
                break
            dx -= 1

        dx = x + 1
        while dx < self.size:
            options.append((dx, y))
            if self.figure_on(dx, y):
                break
            dx += 1

        dy = y - 1
        while dy >= 0:
            options.append((x, dy))
            if self.figure_on(x, dy):
                break
            dy -= 1

        dy = y + 1
        while dy < self.size:
            options.append((x, dy))
            if self.figure_on(x ,dy):
                break
            dy += 1

        return self.filter_options(x, y, options)

    def move_horse(self, x: int, y: int) -> List[Cell]:
        
        options = []
        for dx in [-3, -1, 1, 3]:
            for dy in [-3, -1, 1, 3]:

                if dx == dy:
                    continue

                options.append((x + dx, y + dy))

        return self.filter_options(x, y, options)

    def move_shooter(self, x: int, y: int) -> List[Cell]:

        options = []

        dx = 0
        dy = 0
        while (x + dx >= 0 and x + dx < self.size) or (y + dy >= 0 and y + dy < self.size):
            options.append((x - dx, y - dy))
            options.append((x + dx, y + dy))
            dx += 1
            dy += 1

        return self.filter_options(x, y, options)



    def play_round(self,
                   on_offence: int,
                   pick_method: Callable[[], Cell],
                   move_method: Callable[[], Cell]) -> None:
        
        
        from_x, from_y = pick_method(on_offence)

        figure = self.figure_on(from_x, from_y)
        if figure == "pawn":
            able = self.assign_move[figure](from_x, from_y, on_offence)
        else:
            able = self.assign_move[figure](from_x, from_y)

        des_x, des_y = move_method(able)
        self.move((from_x, from_y), (des_x, des_y))

        if figure == "king":
            self.king_pos[on_move] = (des_x, des_y)



    # --- Text-play
    def text_pick(self, on_move: int) -> Cell:
        """
        Method for picking figure to move
        """

        initial = True
        # Wait for valid move
        while initial or self.board[y][x] == "empty" or self.board[y][x][1] != on_move:

            if not initial:
                print("Invalid cell to pick -- Try another one")

            formatted = input("Select cell to move (x, y): ")
            x, y = self.from_formatted(formatted)

            if self.is_inbound(x, y):
                initial = False
            else:
                print("Out of bounds!")

        return x, y

    def text_move(self, able: List[Cell]) -> Cell:
        """
        Method for picking place to move figure
        """

        initial = True
        while initial or (x, y) not in able:

            if not initial:
                print("Cannot move here!")

            initial = False
            formatted = input("Where would you like to move? (x, y): ")
            x, y = self.from_formatted(formatted)

        return x, y

    def text_play(self) -> None:

        on_move = 1
        on_defence = 2
        danger = False

        while not self.check_mate(on_move, danger):

            self.draw_board()
            print("Player on move: ", on_move)
            print("*" * 20)

            if danger:
                print("*" * 20)
                print("King is endangered")
                print("*" * 20)

            self.play_round(on_move, self.text_pick, self.text_move)

            danger = self.is_endangered(on_defence)
            on_move, on_defence = on_defence, on_move

            for i in range(10):
                print()

        return on_defence

chess = Chess()
chess.text_play()
