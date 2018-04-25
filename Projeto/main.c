#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <limits.h>

#define min(X,Y) (((X) < (Y)) ? (X) : (Y))
#define max(X,Y) (((X) > (Y)) ? (X) : (Y))
#define MAX 10000
#define MAX_COMMAND 200
#define MAX_FILENAME 80

typedef struct {
  unsigned long line;
  unsigned long column;
  double value;
} Point;

typedef struct {
  unsigned long points;
  unsigned long minLine, maxLine;
  unsigned long minCol, maxCol;
  double zero;
  Point point[MAX];
} Matrix;

void loadFromFile (const char filename[]);
void saveToFile (const char filename[]);
int findPoint (unsigned long line, unsigned long column);
void addPoint (unsigned long line, unsigned long col, double val);
void printPoints ();
unsigned long matrixDimension ();
double matrixDensity ();
void matrixInfo ();
void recalculateInfo ();
void removePoint (unsigned long line, unsigned long column);
void removePointByValue (double value);
void updateInfoWithPoint (Point p);
void printLine (unsigned long line);
void printColumn (unsigned long col);
int sortByColumn (const void * a, const void * b);
int sortByLine (const void * a, const void * b);
void sort (int byColumn);
int linesOrder (unsigned long lines[]);
int getLine (double storage[], unsigned long line);
void compress ();

Matrix mx;

int main(int argc, char ** argv) {
  char cmd[MAX_COMMAND + 1];
  char filename[MAX_FILENAME + 1];

  int infoUpdateScheduled = 0;
  unsigned long a, b;
  double c;

  mx.points = 0;
  mx.zero = 0;

  if (argc == 2) {
    strcpy(filename, argv[1]);
    loadFromFile(filename);
  }

  while (fgets(cmd, MAX_COMMAND, stdin) != NULL) {
    if (infoUpdateScheduled && cmd[0] != 'a') {
      recalculateInfo();
      infoUpdateScheduled = 0;
    }

    switch (cmd[0]) {
      case 'a':
        sscanf(cmd, "a %lu %lu %lf", &a, &b, &c);
        addPoint(a, b, c);
        infoUpdateScheduled = 1;
        break;
      case 'p':
        printPoints();
        break;
      case 'i':
        matrixInfo();
        break;
      case 'l':
        sscanf(cmd, "l %lu", &a);
        printLine(a);
        break;
      case 'c':
        sscanf(cmd, "c %lu", &a);
        printColumn(a);
        break;
      case 'o':
        sort(cmd[2] != '\0');
        break;
      case 'z':
        sscanf(cmd, "z %lf", &c);
        mx.zero = c;
        removePointByValue(c);
        break;
      case 's':
        compress();
        break;
      case 'w':
        if (cmd[1] != '\0')
          sscanf(cmd, "w %s", filename);
        saveToFile(filename);
        break;
      case 'q':
        return 0;
    }
  }

  return 0;
}

/**
 * Loads the elements of a matrix from a file.
 */
void loadFromFile (const char filename[]) {
  char s[MAX_COMMAND + 1];
  unsigned long l, c;
  double v;

  FILE *file = fopen(filename, "r");

  while (fgets(s, MAX_COMMAND, file) != NULL) {
    sscanf(s, "%lu %lu %lf", &l, &c, &v);
    addPoint(l, c, v);
  }

  fclose(file);
}

/**
 * Saves the elements of a matrix to a file.
 */
void saveToFile (const char filename[]) {
  int i;
  FILE *file = fopen(filename, "w");

  for (i = 0; i < mx.points; i++)
    fprintf(file, "%lu %lu %lf\n",
      mx.point[i].line,
      mx.point[i].column,
      mx.point[i].value);

  fclose(file);
}

/**
 * Searches for a point in a matrix and returns its position.
 */
int findPoint (unsigned long line, unsigned long column) {
  int i;

  for (i = 0; i < mx.points; i++)
    if (mx.point[i].line == line && mx.point[i].column == column)
      return i;

  return -1;
}

/**
 * Adds a point to a matrix.
 */
void addPoint (unsigned long line, unsigned long col, double val) {
  /* Removes the element. */
  if (val == mx.zero) {
    return removePoint(line, col);
  }

  /* Checks if the element already exists. */
  int i;
  if ((i = findPoint(line, col)) != -1) {
    mx.point[i].value = val;
    return;
  }

  /* Adds the point. */
  mx.point[mx.points].line = line;
  mx.point[mx.points].column = col;
  mx.point[mx.points].value = val;
  mx.points++;
}

/**
 * Prints the list of points of a matrix.
 */
void printPoints () {
  if (mx.points == 0) {
    printf("empty matrix\n");
    return;
  }

  int i;
  for (i = 0; i < mx.points; i++) {
    printf("[%lu;%lu]=%.3lf\n",
      mx.point[i].line,
      mx.point[i].column,
      mx.point[i].value);
  }
}

/**
 * Calculates a matrix's dimension.
 */
unsigned long matrixDimension () {
  return (mx.maxCol - mx.minCol + 1) * (mx.maxLine - mx.minLine + 1);
}

/**
 * Calculates a matrix's density.
 */
double matrixDensity () {
  return (mx.points / (double) matrixDimension()) * 100;
}

/**
 * Prints the matrix information, i.e., the minimum column,
 * the maximum column, the minimum line, the maximum line,
 * the number of points, the dimension and density.
 */
void matrixInfo () {
  if (!mx.points) {
    printf("empty matrix\n");
    return;
  }

  printf("[%lu %lu] [%lu %lu] %lu / %lu = %.3lf%%\n",
    mx.minLine,
    mx.minCol,
    mx.maxLine,
    mx.maxCol,
    mx.points,
    matrixDimension(),
    matrixDensity());
}

/**
 * Removes a point from a matrix knowing its line and column.
 */
void removePoint (unsigned long line, unsigned long column) {
  int i = findPoint(line, column);

  /* The point doesnt't exist. */
  if (i == -1)
    return;

  for (i++; i < mx.points; i++)
    mx.point[i - 1] = mx.point[i];

  mx.points--;
}

/**
 * Removes all points from a matrix knowing their value.
 */
void removePointByValue (double value) {
  int i = 0, j = 0;

  while (i < mx.points) {
    if (mx.point[j].value == value) {
      mx.points--;
    } else {
      mx.point[i] = mx.point[j];
      i++;
    }
    j++;
  }

  recalculateInfo();
}

/**
 * Completely recalculates a matrix's information.
 */
void recalculateInfo () {
  int i;

  mx.minLine = ULONG_MAX;
  mx.minCol = ULONG_MAX;
  mx.maxCol = 0;
  mx.maxLine = 0;

  for (i = 0; i < mx.points; i++) {
    mx.minLine = min(mx.minLine, mx.point[i].line);
    mx.minCol = min(mx.minCol, mx.point[i].column);
    mx.maxLine = max(mx.maxLine, mx.point[i].line);
    mx.maxCol = max(mx.maxCol, mx.point[i].column);
  }
}

/**
 * Gets the values of a line on the matrix.
 */
int getLine (double storage[], unsigned long line) {
  const unsigned long dim = mx.maxCol - mx.minCol + 1;
  int i, count = 0;

  for (i = 0; i < dim; i++)
    storage[i] = mx.zero;

  for (i = 0; i < mx.points; i++) {
    if (mx.point[i].line == line) {
      storage[mx.point[i].column - mx.minCol] = mx.point[i].value;
      count++;
    }
  }

  return count;
}

/**
 * Prints a line.
 */
void printLine (unsigned long line) {
  unsigned long i, dim = mx.maxCol - mx.minCol + 1;
  double values[dim];

  if (line > mx.maxLine || line < mx.minLine || !getLine(values, line)) {
    printf("empty line\n");
    return;
  }

  for (i = 0; i < dim; i++)
    printf(" %.3lf", values[i]);

  printf("\n");
}

/**
 * Prints a column.
 */
void printColumn (unsigned long col) {
  unsigned long int dim = mx.maxLine - mx.minLine + 1, i;
  int empty = 1;
  double values[dim];

  if (col > mx.maxCol || col < mx.minCol) {
    printf("empty column\n");
    return;
  }

  for (i = 0; i < dim; i++) values[i] = mx.zero;
  for (i = 0; i < mx.points; i++) {
    if (mx.point[i].column == col) {
      values[mx.point[i].line - mx.minLine] = mx.point[i].value;
      empty = 0;
    }
  }

  if (empty) {
    printf("empty column\n");
    return;
  }

  for (i = 0; i < dim; i++)
    printf("[%lu;%lu]=%.3lf\n", i + mx.minLine, col, values[i]);
}

/**
 * Sort points by column. Comparison function to qsort.
 */
int sortByColumn (const void * a, const void * b) {
  const Point *p1 = (Point *)a;
  const Point *p2 = (Point *)b;

  if (p1->column < p2->column) {
    return -1;
  } else if (p1->column > p2->column) {
    return +1;
  } else if (p1->column == p2->column) {
    return sortByLine(a, b);
  }

  return 0;
}

/**
 * Sort points by line. Comparison function to qsort.
 */
int sortByLine (const void * a, const void * b) {
  const Point *p1 = (Point *)a;
  const Point *p2 = (Point *)b;

  if (p1->line < p2->line) {
    return -1;
  } else if (p1->line > p2->line) {
    return +1;
  } else if (p1->line == p2->line) {
    return sortByColumn(a, b);
  }

  return 0;
}

/**
 * Sort the points of a matrix.
 */
void sort (int byColumn) {
  if (byColumn)
    qsort(mx.point, mx.points, sizeof(Point), sortByColumn);
  else
    qsort(mx.point, mx.points, sizeof(Point), sortByLine);
}

/**
 * Get the order of lines to compress by their density.
 */
int linesOrder (unsigned long lines[]) {
  unsigned long maxLines = mx.maxLine - mx.minLine + 1,
    i, j, line = 0, dens = 0;
  int densities[maxLines], lineCount = 0;

  for (i = 0; i < maxLines; i++)
    densities[i] = 0;

  for (i = 0; i < mx.points; i++) {
    if (densities[mx.point[i].line - mx.minLine] == 0)
      lineCount++;
    densities[mx.point[i].line - mx.minLine]++;
  }

  for (i = 0; i < lineCount; i++) {
    line = 0;
    dens = 0;

    for (j = 0; j < maxLines; j++) {
      if (densities[j] == dens) {
        line = min(line, j);
      } else if (densities[j] > dens) {
        line = j;
        dens = densities[j];
      }
    }

    lines[i] = line;
    densities[line] = 0;
  }

  for (i = 0; i < lineCount; i++)
    lines[i] += mx.minLine;

  return lineCount;
}

/**
 * Compress a matrix.
 */
void compress () {
  if (matrixDensity() > 50) {
    printf("dense matrix\n");
    return;
  }

  unsigned long matrixHeight = mx.maxLine - mx.minLine + 1,
    columnsCount = mx.maxCol - mx.minCol + 1,
    i, valueI, j, fi, done, o, linesCount, furthest = 0;

  unsigned long order[matrixHeight],
    offset[matrixHeight],
    index[MAX*2];

  double value[MAX*2], line[columnsCount];

  linesCount = linesOrder(order);

  for (i = 0; i < matrixHeight; i++) offset[i] = 0;
  for (i = 0; i < MAX*2; i++) {
    value[i] = mx.zero;
    index[i] = 0;
  }

  /* Iterate over the lines. */
  for (i = 0; i < linesCount; i++) {
    /* Gets the current line. */
    getLine(line, order[i]);

    /* fi is the first non-zero element of the current line. */
    for (fi = 0; line[fi] == mx.zero; fi++);

    /* done indicates if we've finished finding the offset. */
    done = 0;

    /* o is the offset. */
    o = -1;

    while (!done) {
      o++;
      done = 1;
      for (j = fi, valueI = fi+o; valueI < columnsCount+o; valueI++, j++)
        if (value[valueI] != mx.zero && line[j] != mx.zero)
          done = 0;
    }

    offset[order[i] - mx.minLine] = o;

    /* Copies the values to the compressed lists. */
    for (j = fi, valueI = fi+o; valueI < columnsCount+o; valueI++, j++) {
      if (line[j] != mx.zero) {
        value[valueI] = line[j];
        index[valueI] = order[i];
      }
    }

    furthest = max(furthest, valueI);
  }

  printf("value =");
  for (i = 0; i < furthest; i++) printf(" %.3lf", value[i]);
  printf("\nindex =");
  for (i = 0; i < furthest; i++) printf(" %lu", index[i]);
  printf("\noffset =");
  for (i = 0; i < matrixHeight; i++) printf(" %lu", offset[i]);
  printf("\n");
}
