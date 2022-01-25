from pygame.locals import *
import pygame as pg
import chess
import sys
from type_aliases import *


WIDTH = 600
HEIGHT = 600
STATUS_BAR_HEIGHT = 50


WHITE = (255, 255, 255)
BLACK = (0, 0, 0)
RED = (180, 0, 0)
BG1 = (206, 189, 121)
BG2 = (84, 102, 75)


class ChessVisualize:

    def __init__(self) -> None:

        # initializing pygame window
        self.fps = 30
        self.CLOCK = pg.time.Clock()
        pg.init()
        pg.display.set_caption("Chess with Ejdam")
        self.screen = pg.display.set_mode((WIDTH, HEIGHT + STATUS_BAR_HEIGHT))

        self.status_bar_text = ""
        self.font = pg.font.Font(None, 32)
        self.engine = chess.Chess()
        self.selected = None
        self.possible_moves = None

        self.row_count = len(self.engine.board)
        self.column_count = len(self.engine.board[0])

        self.grid_node_width = WIDTH // self.column_count
        self.grid_node_height = HEIGHT // self.row_count

        self.figure_width = int(self.grid_node_width * 0.6)
        self.figure_height = int(self.grid_node_height * 0.6)

        self.margin = (self.grid_node_width - self.figure_width) // 2

        self.initialize_grid()
        self.redraw_board()

    def initialize_grid(self, endangered = None) -> None:
        """
        Creates chess grid

        endangered - Is a list of cells which are endangered
                   - It is used only when visualizing player options to attack
        """
        self.screen.fill(BG1)
        dx = 0
        dy = 0
        fill = True
        for y in range(self.row_count):
            for x in range(self.column_count):

                outline = 0 if fill else 1
                pg.draw.rect(self.screen, BG2, (dx, dy, self.grid_node_width, self.grid_node_height), outline)

                if endangered and (x, y) in endangered:

                    if self.engine.board[y][x] == "empty":  # If there is empty cell, we wanna show a little circle
                        center_x = dx + self.grid_node_width // 2
                        center_y = dy + self.grid_node_height // 2
                        pg.draw.circle(self.screen, RED, (center_x, center_y), 10, 0)
                    else:   # If there is oppponent's figure, we wanna fill whole cell with red
                        pg.draw.rect(self.screen, RED, (dx, dy, self.grid_node_width, self.grid_node_height), 0)

                fill = not fill
                dx += self.grid_node_width

            fill = not fill

            dx = 0
            dy += self.grid_node_height

        pg.display.update()        

    def redraw_board(self, endangered = None) -> None:
        """
        Show all changes made to the board

        endangered - Is a list of cells which are endangered
                   - It is used only when visualizing player options to attack
        """
        self.initialize_grid(endangered)
        dx = 0
        dy = 0
        for x in range(self.column_count):
            for y in range(self.row_count):

                if self.engine.board[y][x] == "empty":
                    dy += self.grid_node_height
                    continue

                elif self.engine.board[y][x][1] == 1:
                    pg.draw.rect(self.screen, WHITE, (dx + self.margin, dy + self.margin, self.figure_width, self.figure_height))

                else:
                    pg.draw.rect(self.screen, BLACK, (dx + self.margin, dy + self.margin, self.figure_width, self.figure_height))

                dy += self.grid_node_height
            dx += self.grid_node_width
            dy = 0

        pg.display.update()

    def play_round(self, click_coords: Tuple[int, int], on_offence: int) -> None:

        click_coords = pg.mouse.get_pos()
        cell = self.cell_pick(click_coords)

        if not self.selected:

            # Could not assign cell
            if not cell:
                return

            x, y = cell
            figure = self.engine.figure_on(x, y)

            # Want to move empty cell or opponent's cell
            if self.engine.board[y][x][1] != on_offence:
                return False
            
            if figure == "pawn":
                self.possible_moves = self.engine.assign_move[figure](x, y, on_offence)
            else:
                self.possible_moves = self.engine.assign_move[figure](x, y)

            self.selected = cell, figure
            self.redraw_board(self.possible_moves)
            if not figure:
                self.update_status_bar(f"Empty cell")
            else:
                self.update_status_bar(f"Chosen cell: {self.engine.from_cell(cell)} ({figure})")

        else:

            # If you selected same cell 2-times in a row -> unselect row
            if cell == self.selected[0]:
                self.selected = None
                self.possible_moves = None
                return False

            # Want to move on cell where cannot be moved
            if cell not in self.possible_moves:
                return False

            figure = self.selected[1]
            self.engine.move(self.selected[0], cell)
            if figure == "king":
                self.engine.king_pos[on_move] = cell

            self.selected = None
            self.possible_moves = None

        return True

    def cell_pick(self, click_coords: Tuple[int, int]) -> Cell:

        mx, my = click_coords
        if my > HEIGHT:  # Clicked at status bar
            return

        return mx // self.grid_node_width, my // self.grid_node_height

    def update_status_bar(self, to_show: str) -> None:

        self.status_bar_text = to_show
        x_center, y_center = self.screen.get_rect().center
        y_center += HEIGHT // 2
        text = self.font.render(to_show, True, BLACK, BG1)
        text_rect = text.get_rect(center = (x_center, y_center))

        # Overdraw previous text
        pg.draw.rect(self.screen, BG1, (0, HEIGHT, WIDTH, STATUS_BAR_HEIGHT))
        self.screen.blit(text, text_rect)

        pg.display.update()

    def run(self) -> None:

        on_offence, on_defence = 1, 2
        player_color = {1: "White", 2: "Black"}
        self.update_status_bar(f"Player on move: {player_color[on_offence]}")

        while True:
            for event in pg.event.get():
                if event.type == QUIT:
                    # Window quit
                    pg.quit()
                    sys.exit()

                elif event.type is MOUSEBUTTONDOWN:
                    
                    coords = pg.mouse.get_pos()
                    success = self.play_round(coords, on_offence)
                    finished_move = self.possible_moves == None

                    if success and finished_move:
                        self.redraw_board()
                        on_offence, on_defence = on_defence, on_offence
                        self.update_status_bar(f"Player on move: {player_color[on_offence]}")

            pg.display.update()
            self.CLOCK.tick(self.fps)


vis = ChessVisualize()
vis.run()
