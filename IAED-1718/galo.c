#include <stdio.h>

#define BALL 'o'
#define CROSS 'x'

char board[9] = {'1', '2', '3', '4', '5', '6', '7', '8', '9'};

void clear () {
  system("clear");
}

void print () {
  for (int i = 0; i < 3; i++) {
    printf("|-------|-------|-------|\n");
    printf("|   %c   |   %c   |   %c   |\n", board[i*3], board[i*3+1], board[i*3+2]);
  }

  printf("|-------|-------|-------|\n");
}

int filled () {
  int filledSpaces = 0;

  for (int i = 0; i < 9; i++)
    if (board[i] == BALL || board[i] == CROSS)
      filledSpaces++;

  return filledSpaces == 9;
}

void doMove (int player) {
  while (1) {
    int pos;
    printf("Player %c, please insert the position: ", player ? BALL : CROSS);

    if (scanf("%d", &pos) != 1) {
      printf("Invalid position.\n");
      scanf("%*s");
    } else if (pos < 1 || pos > 9) {
      printf("Invalid position.\n");
    } else if (board[pos - 1] == BALL || board[pos - 1] == CROSS) {
      printf("Invalid position.\n");
    } else {
      board[pos - 1] = player ? BALL : CROSS;
      return;
    }
  }
}

int anyWinner () {
  if (board[0] == board[1] && board[1] == board[2])
    return 1;
  else if (board[3] == board[4] && board[4] == board[5])
    return 1;
  else if (board[6] == board[7] && board[7] == board[8])
    return 1;
  else if (board[0] == board[4] && board[4] == board[8])
    return 1;
  else if (board[2] == board[4] && board[4] == board[6])
    return 1;
  else if (board[0] == board[3] && board[3] == board[6])
    return 1;
  else if (board[1] == board[4] && board[4] == board[7])
    return 1;
  else if (board[2] == board[5] && board[5] == board[8])
    return 1;
  else
    return 0;
}

int main () {
  int player = 0;

  while (!filled() && !anyWinner()) {
    clear();
    print();
    doMove(player);
    player = !player;
  }

  clear();
  print();

  if (!anyWinner()) {
    printf("No one won. RIP.\n");
  } else {
    printf("Player %c won. Congrats!\n", !player ? BALL : CROSS);
  }

  return 0;
}
