### Scurses

- [x] RGB to color code
- [x] Homemade key polling with more keys than JLine
- [x] Handle resize event
- [x] Custom ML for RichText
- [x] Clipping mode
- [ ] Buffer concept where you can put things before printing them
      -> widgets will update first, we know their height, then we can draw
         from outside the widget
  
### Onions:

- [x] Remove Options' `get()`
- [x] Generalize directional methods.
- [x] Don't link panels in `split()`, do it in different constructor
- [x] Write `getNext(direction)` for focus navigation
- [x] Remove resizing from split, resizing will be done dynamically at redraw
- [x] Remove position, do everything with relative translations (remove absolute)
- [x] Update dimensions has to recursively go down and update right with stuff
- [x] Debug mode for frame
- [x] Focus ring around panel
- [x] Color schemes
- [x] All widgets take `Varying[T] for parameters
    - Adds themselves as subscribers for change
    - Callback is just redraw for now
- [x] Split panel into tabs
- [x] Make all plot types handle negative values and indices
- [x] Generalize graph grid + legends
- [x] Only redraw needed
- [x] Detect height changes -> request redraw
- [x] Make all plot types handle floats
- [ ] `split()` version with weight
- [ ] Scrollbars for long content
- [ ] Support multiple sets of values in scatter plot
- [ ] Generalize plot coordinate helpers
- [ ] Convert current things to RichText
- [ ] Handle out of bound values for plots
- [ ] Transition from FramePanel to SplitLayout
    - Inherit from new Layout class with markAll, redraw
    - ColumnLayout
    - AbsoluteLayout
- [ ] In widget `draw()`, compute all lengths first to determine if enough room
    -> Display "Not enough room"
- [ ] Graph widgets have optional height parameter
- Widgets:
    - [x] Bar graph
    - [x] Big text display
    - [x] SevenSegment
    - [x] Input
    - [x] Radio and RadioGroup
    - [x] BitMap
    - [x] Scatter plot
    - [x] HeatMap
    - [x] Slider
    - [x] CheckBox
    - [x] Histogram
    - [ ] Progressbar, displays percentage
    - [ ] TextBox
        - Console, grabs stdout and stderr
        - Terminal, along with an input field, spawns a bash