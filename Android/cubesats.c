#include <iostream>

int main()
{
  for(int n = 99; n >= 0; n--)
  {
    if(n>2)
    {
      std::cout << n << " cubesats on low earth orbit, " << n << " cubesats" << std::endl;
      std::cout << "Shoot one down with a laser, it de-orbits, " << n-1 << " cubesats" << std::endl;
    }
    else if(n==2)
    {
        std::cout << n << " cubesats on low earth orbit, " << n << " cubesats" << std::endl;
        std::cout << "Shoot one down with a laser, it de-orbits, " << n-1 << " cubesat" << std::endl;
    }
    else if(n==1)
    {
        std::cout << "One cubesat on low earth orbit, one cubesat" << std::endl;
        std::cout << "Shoot one down with a laser, it de-orbits, no cubesats" << std::endl;
    }
    else
    {
      std::cout << "No more cubesats on low earth orbit, no more cubesats!" << std::endl;
    }
  }
  return 0;
}
