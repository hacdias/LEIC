#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define min(X,Y) (((X) < (Y)) ? (X) : (Y))
#define max(X,Y) (((X) > (Y)) ? (X) : (Y))
#define MAX 10000
#define MAX_COMMAND 100
#define MAX_FILENAME 300

typedef struct {
  unsigned int line;
  unsigned int column;
  double value;
} Point;

typedef struct {
  unsigned int points;
  unsigned int minLine, maxLine;
  unsigned int minCol, maxCol;
  Point point[MAX];
} Matrix;

int readLine (char *s, int n, FILE *stream);
void loadFromFile (Matrix *m, const char *filename);
void saveToFile (Matrix *m, const char *filename);
Point * findPoint (Matrix *m, int line, int column);
void addPoint (Matrix *m, unsigned int line, unsigned int col, double val);
void printPoints (Matrix *m);
int matrixDimension (Matrix *m);
double matrixDensity (Matrix *m);
void matrixInfo (Matrix *m);
void removePoint (Matrix *m, int line, int column);
void removePointByValue (Matrix *m, double value);
void updateInfoWithPoint (Matrix *m, Point *p);
void recalculateInfo (Matrix *m);
void printLine (Matrix *m, unsigned int line);
void printColumn (Matrix *m, unsigned int col);
int sortByColumn (const void * a, const void * b);
int sortByLine (const void * a, const void * b);
void sort (Matrix *m, int byColumn);
int linesOrder (int *lines, Matrix *m);
int getLine (double *storage, Matrix *m, unsigned int line);
void compress (Matrix *m);

int main(int argc, char ** argv) {
  char cmd[MAX_COMMAND + 1];
  char filename[MAX_FILENAME + 1];

  int len = 0, a, b;
  double c;
  
  Matrix mx;
  mx.points = 0;
  mx.minLine = MAX;
  mx.maxLine = 0;
  mx.minCol = MAX;
  mx.maxCol = 0;

  if (argc == 2) {
    strcpy(filename, argv[1]);
    loadFromFile(&mx, filename);
  }

  while ((len = readLine(cmd, MAX_COMMAND, stdin))) {
    switch (cmd[0]) {
      case 'a':
        sscanf(cmd, "a %d %d %lf", &a, &b, &c);
        addPoint(&mx, a, b, c);
        break;
      case 'p':
        printPoints(&mx);
        break;
      case 'i':
        matrixInfo(&mx);
        break;
      case 'l':
        sscanf(cmd, "l %d", &a);
        printLine(&mx, a);
        break;
      case 'c':
        sscanf(cmd, "c %d", &a);
        printColumn(&mx, a);
        break;
      case 'o':
        sort(&mx, len != 1);
        break;
      case 'z':
        sscanf(cmd, "z %lf", &c);
        removePointByValue(&mx, c);
        break;
      case 's':
        compress(&mx);
        break;
      case 'w':
        if (len != 1) {
          sscanf(cmd, "w %s", filename);
        }
        saveToFile(&mx, filename);
        break;
      case 'q':
        return 0;
    }
  }

  return 0;
}

/**
 * Reads a line from a stream until the end of line,
 * end of file or n is reached and stores it into s.
 * Returns the number of read characters.
 */
int readLine (char *s, int n, FILE *stream) {
  char c;
  int i = 0;
  while ((c = fgetc(stream)) != '\n' && i < n && c != EOF) {
    s[i] = c;
    i++;
  }
  s[i] = '\0';
  return i;
}

/**
 * Loads the elements of a matrix from a file.
 */
void loadFromFile (Matrix *m, const char *filename) {
  FILE *file = fopen(filename, "r");
  char s[MAX_COMMAND + 1];
  unsigned int l, c;
  double v;

  while (readLine(s, MAX_COMMAND, file)) {
    sscanf(s, "%d %d %lf", &l, &c, &v);
    addPoint(m, l, c, v);
  }

  fclose(file);
}

/**
 * Saves the elements of a matrix to a file.
 */
void saveToFile (Matrix *m, const char *filename) {
  FILE *file = fopen(filename, "w");

  for (int i = 0; i < m->points; i++) {
    fprintf(file, "%d %d %lf\n", m->point[i].line, m->point[i].column, m->point[i].value);
  }

  fclose(file);
}

/**
 * Searches for a point in a matrix and returns its pointer.
 */
Point * findPoint (Matrix *m, int line, int column) {
  for (int i = 0; i < m->points; i++)
    if (m->point[i].line == line && m->point[i].column == column)
      return &m->point[i];

  return NULL;
}

/**
 * Adds a point to a matrix.
 */
void addPoint (Matrix *m, unsigned int line, unsigned int col, double val) {
  // Removes the element.
  if (val == 0) {
    return removePoint(m, line, col);
  }

  // Checks if the element already exists.
  Point *p;
  if ((p = findPoint(m, line, col)) != NULL) {
    p->value = val;
    return;
  }

  // Adds the point.
  m->point[m->points].line = line;
  m->point[m->points].column = col;
  m->point[m->points].value = val;
  updateInfoWithPoint(m, &m->point[m->points]);
  m->points++;
}

/**
 * Prints the list of points of a matrix.
 */
void printPoints (Matrix *m) {
  if (m->points == 0) {
    printf("empty matriz\n");
    return;
  }

  for (int i = 0; i < m->points; i++) {
    printf("[%d;%d]=%.3lf\n",
      m->point[i].line,
      m->point[i].column,
      m->point[i].value);
  }
}

/**
 * Calculates a matrix's dimension.
 */
int matrixDimension (Matrix *m) {
  return (m->maxCol - m->minCol + 1) * (m->maxLine - m->minLine + 1);
}

/**
 * Calculates a matrix's density.
 */
double matrixDensity (Matrix *m) {
  return (m->points / (double) matrixDimension(m)) * 100;
}

/**
 * Prints the matrix information, i.e., the minimum column,
 * the maximum column, the minimum line, the maximum line,
 * the number of points, the dimension and density.
 */
void matrixInfo (Matrix *m) {
  int dim = matrixDimension(m);
  double dens = matrixDensity(m);

  printf("[%d %d] [%d %d] %d / %d = %.3lf%%\n",
    m->minLine,
    m->minCol,
    m->maxLine,
    m->maxCol,
    m->points,
    dim,
    dens);
}

/**
 * Removes a point from a matrix knowing its line and column.
 */
void removePoint (Matrix *m, int line, int column) {
  int i;

  for (i = 0; (m->point[i].line != line || m->point[i].column != column); i++);
  for (i = i + 1; i < m->points; i++) {
    m->point[i - 1] = m->point[i];
  }

  m->points--;
  recalculateInfo(m);
}

/**
 * Removes all points from a matrix knowing their value.
 */
void removePointByValue (Matrix *m, double value) {
  int i = 0, j = 0;

  while (i < m->points) {
    if (m->point[j].value == value) {
      j++;
      m->points--;
    } else {
      m->point[i] = m->point[j];
      i++;
      j++;
    }
  }

  recalculateInfo(m);
}

/**
 * Updates a matrix's information according to a point's
 * coordinates.
 */
void updateInfoWithPoint (Matrix *m, Point *p) {
  m->minLine = min(m->minLine, p->line);
  m->minCol = min(m->minCol, p->column);
  m->maxLine = max(m->maxLine, p->line);
  m->maxCol = max(m->maxCol, p->column);
}

/**
 * Completely recalculates a matrix's information.
 */
void recalculateInfo (Matrix *m) {
  for (int i = 0; i < m->points; i++)
    updateInfoWithPoint(m, &m->point[i]);
}

/**
 * Prints a line.
 */
void printLine (Matrix *m, unsigned int line) {
  int dim = m->maxCol - m->minCol + 1, i, empty = 1;
  double values[dim];

  if (line > m->maxLine || line < m->minLine) {
    printf("empty line\n");
    return;
  }

  for (i = 0; i < dim; i++) values[i] = 0;
  for (i = 0; i < m->points; i++) {
    if (m->point[i].line == line) {
      values[m->point[i].column - m->minCol] = m->point[i].value;
      empty = 0;
    }
  }

  if (empty) {
    printf("empty line\n");
    return;
  }

  for(i = 0; i < dim; i++) printf(" %.3lf", values[i]);
  printf("\n");
}

/**
 * Prints a column.
 */
void printColumn (Matrix *m, unsigned int col) {
  int dim = m->maxLine - m->minLine + 1, i, empty = 1;
  double values[dim];

  if (col > m->maxCol || col < m->minCol) {
    printf("empty column\n");
    return;
  }

  for (i = 0; i < dim; i++) values[i] = 0;
  for (i = 0; i < m->points; i++) {
    if (m->point[i].column == col) {
      values[m->point[i].line - m->minLine] = m->point[i].value;
      empty = 0;
    }
  }

  if (empty) {
    printf("empty column\n");
    return;
  }

  for(i = 0; i < dim; i++) {
    printf("[%d;%d]=%.3lf\n", i, col, values[i]);
  }
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
void sort (Matrix *m, int byColumn) {
  if (byColumn) {
    qsort(m->point, m->points, sizeof(Point), sortByColumn);
  } else {
    qsort(m->point, m->points, sizeof(Point), sortByLine);
  }
}

/**
 * Get the order of lines to compress by their density.
 */
int linesOrder (int *lines, Matrix *m) {
  int maxLines = m->maxLine - m->minLine + 1,
    densities[maxLines], i, j, lineCount = 0,
    line = 0, dens = 0;

  for (i = 0; i < maxLines; i++)
    densities[i] = 0;

  for (i = 0; i < m->points; i++)
    densities[m->point[i].line - m->minLine]++;

  for (i = 0; i < maxLines; i++)
    if (densities[i] != 0) lineCount++;

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
    lines[i] += m->minLine;

  return lineCount;
}

/**
 * Gets the values of a line on the matrix.
 */
int getLine (double *storage, Matrix *m, unsigned int line) {
  int counter = 0, i;

  for (i = 0; i < m->points; i++) {
    if (m->point[i].line == line) {
      counter++;
      storage[m->point[i].column - m->minCol] = m->point[i].value;
    }
  }

  return counter;
}

/**
 * Compress a matrix.
 */
void compress (Matrix *m) {
  if (matrixDensity(m) > 50) {
    printf("dense matrix\n");
    return;
  }

  int matrixHeight = m->maxLine - m->minLine + 1;
  int order[matrixHeight];
  int linesCount = linesOrder(order, m);
  int columnsCount = m->maxCol - m->minCol + 1;

  int offset[matrixHeight];
  double value[MAX];
  int index[MAX];

  for (int i = 0; i < matrixHeight; i++) offset[i] = 0;
  for (int i = 0; i < MAX; i++) {
    value[i] = 0;
    index[i] = 0;
  }

  int valueI;
  double line[columnsCount];

  int i, j, count, fi, done, o;

  // Iterate over the lines.
  for (i = 0; i < linesCount; i++) {
    // Resets the line values.
    for (j = 0; j < columnsCount; j++)
      line[j] = 0;
    
    // Gets the current line.
    count = getLine(line, m, order[i]);

    // fi is the first non-zero element of the current line.
    for (fi = 0; line[fi] == 0; fi++);

    // Done indicates if we've finished finding the offset.
    // o is the offset.
    done = 0;
    o = -1;

    while (!done) {
      o++;
      done = 1;
      for (j = fi, valueI = fi+o; valueI < columnsCount+o; valueI++, j++)
        if (value[valueI] != 0 && line[j] != 0)
          done = 0;
    }

    offset[order[i] - m->minLine] = o;

    // Copies the values to the compressed lists.
    for (j = fi, valueI = fi+o; valueI < columnsCount+o; valueI++, j++) {
      if (line[j] != 0) {
        value[valueI] = line[j];
        index[valueI] = order[i];
      }
    }
  }

  printf("value = ");
  for (int i = 0; i < valueI; i++) printf("%.3lf ", value[i]);
  printf("\nindex = ");
  for (int i = 0; i < valueI; i++) printf("%d ", index[i]);
  printf("\noffset = ");
  for (int i = 0; i < matrixHeight; i++) printf("%d ", offset[i]);
  printf("\n");
}
