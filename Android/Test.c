#include <iostream>

void main()
{
  for(int n = 99; n => 0; n--)
  {
    if(n!=0)
    {
      std::cout << n << " cubesats on low earth orbit, " << n << " cubesats" << std::endl;
      std::cout << "Shoot one down with a laser, it de-orbits, " << n-- << " cubesats";
    }
    else
    {
      std::cout << "No more cubesates on low earth orbit, no more cubesats!" << std::endl;
    }
  }
}
