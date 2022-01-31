

with open("gui.py", "r") as f:

    content = f.read()
    new = content.replace("grid_node_width", "NODE_WIDTH").replace("grid_node_height", "NODE_HEIGHT")

    with open("new.py", "w") as n:

        n.write(new)