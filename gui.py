from pygame.locals import *
import pygame as pg
import chess
import sys
from typing import Tuple


Color = Tuple[int, int, int]
WIDTH = 600
HEIGHT = 600

BG1 = (206, 189, 121)
BG2 = (84, 102, 75)


class ChessVisualize:

    def __init__(self) -> None:

        # initializing pygame window
        self.fps = 30
        self.CLOCK = pg.time.Clock()
        pg.init()
        pg.display.set_caption("Chess with Ejdam")
        self.screen = pg.display.set_mode((WIDTH, HEIGHT))

        self.engine = chess.Chess()
        self.board = self.engine.board

        self.row_count = len(self.board)
        self.column_count = len(self.board[0])

        self.grid_node_width = WIDTH // self.column_count
        self.grid_node_height = HEIGHT // self.row_count

        self.initialize_grid()

    def initialize_grid(self) -> None:

        self.screen.fill(BG1)
        dx = 0
        dy = 0
        fill = True
        for y in range(self.row_count):
            for x in range(self.column_count):
                self.createSquare(dx, dy, BG2, fill)
                fill = not fill
                dx += self.grid_node_width

            fill = not fill

            dx = 0
            dy += self.grid_node_height

        pg.display.update()

    def createSquare(self, x: int, y: int, color: Color, fill: bool) -> None:

        outline = 0 if fill else 1

        pg.draw.rect(self.screen, color, (x, y, self.grid_node_width, self.grid_node_height), outline)

    def run(self) -> None:

        while True:
            for event in pg.event.get():
                if event.type == QUIT:
                    # Window quit
                    pg.quit()
                    sys.exit()

                elif event.type is MOUSEBUTTONDOWN:
                    # Click
                    pass

            pg.display.update()
            self.CLOCK.tick(self.fps)


vis = ChessVisualize()
vis.run()
