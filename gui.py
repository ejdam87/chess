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
RED = (147, 54, 64)
BLUE = (99, 95, 173)
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


        self.player_color = {1: "White", 2: "Black"}
        self.status_bar_text = ""
        self.font = pg.font.Font(None, 32)
        self.engine = chess.Chess()
        self.selected = None
        self.possible_moves = None

        self.row_count = len(self.engine.board)
        self.column_count = len(self.engine.board[0])

        self.NODE_WIDTH = WIDTH // self.column_count
        self.NODE_HEIGHT = HEIGHT // self.row_count

        self.figure_width = int(self.NODE_WIDTH * 0.6)
        self.figure_height = int(self.NODE_HEIGHT * 0.6)

        self.margin = (self.NODE_WIDTH - self.figure_width) // 2

        self.textures = {
            "pawn": ((self.load_texture("textures/pawn_white.png")), self.load_texture("textures/pawn_black.png")),
            "knight" : ((self.load_texture("textures/knight_white.png")), self.load_texture("textures/knight_black.png")),
            "rook" : ((self.load_texture("textures/rook_white.png")), self.load_texture("textures/rook_black.png")),
            "bishop" : ((self.load_texture("textures/bishop_white.png")), self.load_texture("textures/bishop_black.png")),
            "king" : ((self.load_texture("textures/king_white.png")), self.load_texture("textures/king_black.png")),
            "queen" : ((self.load_texture("textures/queen_white.png")), self.load_texture("textures/queen_black.png"))
        }

        self.initialize_grid()
        self.redraw_board()

    def load_texture(self, path: str):

        im = pg.image.load(path)
        im_width, im_height = im.get_size()

        if im_height > im_width:
            ratio = im_width / im_height
        else:
            ratio = im_height / im_width

        texture_height = int(self.NODE_HEIGHT * 0.8)
        texture_width = int(texture_height * ratio)

        texture = pg.transform.smoothscale(im, (texture_width, texture_height))
        return texture

    def initialize_grid(self, endangered = None, selected_cell = None) -> None:
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
                if (x, y) == selected_cell:
                    pg.draw.rect(self.screen, BLUE, (dx, dy, self.NODE_WIDTH, self.NODE_HEIGHT), 0)
                else:
                    pg.draw.rect(self.screen, BG2, (dx, dy, self.NODE_WIDTH, self.NODE_HEIGHT), outline)

                if endangered and (x, y) in endangered:

                    if self.engine.board[y][x] == "empty":  # If there is empty cell, we wanna show a little circle
                        center_x = dx + self.NODE_WIDTH // 2
                        center_y = dy + self.NODE_HEIGHT // 2
                        pg.draw.circle(self.screen, RED, (center_x, center_y), 10, 0)
                    else:   # If there is oppponent's figure, we wanna fill whole cell with red
                        pg.draw.rect(self.screen, RED, (dx, dy, self.NODE_WIDTH, self.NODE_HEIGHT), 0)

                fill = not fill
                dx += self.NODE_WIDTH

            fill = not fill

            dx = 0
            dy += self.NODE_HEIGHT

        pg.display.update()        

    def redraw_board(self, endangered = None, selected_cell = None) -> None:
        """
        Show all changes made to the board

        endangered - Is a list of cells which are endangered
                   - It is used only when visualizing player options to attack
        """
        self.initialize_grid(endangered, selected_cell)
        dx = 0
        dy = 0
        for x in range(self.column_count):
            for y in range(self.row_count):

                if self.engine.board[y][x] == "empty":
                    dy += self.NODE_HEIGHT
                    continue

                figure = self.engine.figure_on(x, y)
                texture = self.textures[figure][self.engine.board[y][x][1] - 1]
                self.screen.blit(texture, (dx + self.NODE_WIDTH // 4, dy + self.NODE_WIDTH // 10))

                dy += self.NODE_HEIGHT
            dx += self.NODE_WIDTH
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
            self.redraw_board(self.possible_moves, cell)
            if not figure:
                self.update_status_bar(f"Empty cell")
            else:
                self.update_status_bar(f"Chosen cell: {self.engine.from_cell(cell)} ({figure})")


        # --- If player has already selected figure to move
        else:

            # --- Invalid moves

            # If you selected same cell 2-times in a row -> unselect row
            if cell == self.selected[0]:
                self.selected = None
                self.possible_moves = None
                self.redraw_board()
                self.update_status_bar(f"Player on move: {self.player_color[on_offence]}")
                return False

            if not self.engine.check_if_possible_move(on_offence, self.selected[0], cell):
                self.update_status_bar("Cannot move there because of endangering the king")
                return False

            if self.engine.danger and (not self.engine.erased_danger(on_offence, self.selected[0], self.possible_moves)):
                self.update_status_bar("Not avoiding chess mate")
                return False

            # Want to move on cell where cannot be moved
            if cell not in self.possible_moves:
                return False

            # --- Invalid moves

            # Actual moving
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

        return mx // self.NODE_WIDTH, my // self.NODE_HEIGHT

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
        self.update_status_bar(f"Player on move: {self.player_color[on_offence]}")

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
                        self.update_status_bar(f"Player on move: {self.player_color[on_offence]}")

                    self.engine.danger = self.engine.is_endangered(on_offence)
                    if self.engine.check_mate(on_offence, self.engine.danger):
                        self.update_status_bar(f"Chess mate!")

            pg.display.update()
            self.CLOCK.tick(self.fps)



vis = ChessVisualize()
vis.run()
