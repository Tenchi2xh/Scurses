### Scurses

- [x] RGB to color code
- [x] Homemade key polling with more keys than JLine
- [x] Handle resize event
- [x] Custom ML for RichText
- [ ] Clipping mode
  
### Onions:

- [x] Remove Options .get()
- [x] Generalize directional methods.
- [x] Don't link panels in split(), do it in different constructor
- [x] Write .getNext(direction) for focus navigation
- [x] Remove resizing from split, resizing will be done dynamically at redraw
- [x] Remove position, do everything with relative translations (remove absolute)
- [x] Update dimensions has to recursively go down and update right with stuff
- [x] Debug mode for frame
- [x] Focus ring around panel
- [x] Color schemes
- [x] All widgets take Varying[T] for parameters
    - Adds themselves as subscribers for change
    - Callback is just redraw for now
- [x] Split panel into tabs
- [x] Make all plot types handle negative values and indices
- [x] Generalize graph grid + legends
- [ ] .split() version with weight
- [ ] Scrollbars for long content
- [ ] Support multiple sets of values in scatter plot
- [ ] Make all plot types handle floats
- [ ] Generalize plot coordinate helpers
- [ ] Convert current things to RichText
- [ ] Only redraw needed
- Widgets:
    - [x] Bar graph
    - [x] Big text display
    - [x] SevenSegment
    - [x] Input
    - [x] Radio and RadioGroup
    - [x] BitMap
    - [x] Scatter plot
    - [x] HeatMap
    - [ ] Progressbar, displays percentage
    - [ ] Histogram
    - [ ] TextBox
    - [ ] CheckBox