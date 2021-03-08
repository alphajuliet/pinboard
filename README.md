# pinboard

Code to read in a Pinboard JSON export, convert it to an Ubergraph graph, and export to GraphML for visualisation.

The graph is set up with all bookmarks and tags as nodes, and edges connecting each bookmark to each of its tags.

The GraphML can be read into [Gephi](https://www.gephi.com/), for example, 
and have a layout applied to it, and formatters to highlight the highest degree nodes.

## Usage

See the code in `src/pinboard/core.clj`.

## License

Copyright © 2021 Andrew Joyner

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
