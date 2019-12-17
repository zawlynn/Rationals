package board

import board.Direction.*

fun createSquareBoard(width: Int): SquareBoard {
    val sq = SquareBoardImpl(width)
    sq.cells = createEmptyBoard(width)
    return sq
}

fun <T> createGameBoard(width: Int): GameBoard<T> {
    val gb = GameBoardImpl<T>(width)
    gb.cells = createEmptyBoard(width)
    gb.cells.forEach {
        it.forEach {cell ->
            gb.cellValues += cell to null
        }
    }
    return gb
}
open class GameBoardImpl<T>(override val width: Int):SquareBoardImpl(width),GameBoard<T>{
    val cellValues = mutableMapOf<Cell,T?>()

    override fun get(cell: Cell): T? {
        return cellValues.get(cell)
    }

    override fun set(cell: Cell, value: T?) {
        cellValues += cell to value
    }

    override fun filter(predicate: (T?) -> Boolean): Collection<Cell> {
       return cellValues.filterValues{
            predicate.invoke(it)
        }.keys
    }

    override fun find(predicate: (T?) -> Boolean): Cell? {
       return cellValues.filter {
            predicate.invoke(it.value)
        }.keys.first()
    }

    override fun any(predicate: (T?) -> Boolean): Boolean {
       return cellValues.values.any(predicate)
    }

    override fun all(predicate: (T?) -> Boolean): Boolean {
        return cellValues.values.all(predicate)
    }

}
open class SquareBoardImpl(override val width: Int) : SquareBoard {
    var cells: Array<Array<Cell>> = arrayOf(arrayOf())
    override fun getCellOrNull(i: Int, j: Int): Cell? =
            when {
                i > width || j > width || i == 0 || j == 0 -> null
                else -> getCell(i, j)
            }

    override fun getCell(i: Int, j: Int): Cell = cells[i - 1][j - 1]

    override fun getAllCells(): Collection<Cell> = IntRange(1, width)
            .flatMap { i ->
                IntRange(1, width)
                        .map { j -> getCell(i, j) }
            }.toList()

    override fun getRow(i: Int, jRange: IntProgression): List<Cell> {
        return when {
            jRange.last > width -> IntRange(jRange.first, width).map { j ->
                getCell(i, j)
            }.toList()

            else -> jRange.map { j ->
                getCell(i, j)
            }.toList()
        }
    }

    override fun getColumn(iRange: IntProgression, j: Int): List<Cell> {
        return when {
            iRange.last > width -> IntRange(iRange.first, width).map {
                getCell(it, j)
            }.toList()

            else -> iRange.map { i ->
                getCell(i, j)
            }.toList()
        }
    }

    override fun Cell.getNeighbour(direction: Direction): Cell? {
       return when(direction){
            UP -> getCellOrNull(i-1,j)
            LEFT -> getCellOrNull(i,j-1)
            DOWN -> getCellOrNull(i+ 1 ,j)
           RIGHT -> getCellOrNull(i, j +1)
        }
    }

}


private fun createEmptyBoard(width : Int):Array<Array<Cell>>{
    var board = arrayOf<Array<Cell>>()
    for ( i in 1..width){
        var array = arrayOf<Cell>()
        for( j in 1..width){
            array += Cell(i,j)
        }
        board += array
    }
    return board
}


